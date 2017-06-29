package org.knime.data.world.restful

import java.io.BufferedOutputStream
import java.io.File
import java.io.OutputStreamWriter
import java.net.URI
import java.nio.charset.Charset
import java.nio.file.Files

import org.knime.base.node.io.csvwriter.CSVWriter
import org.knime.base.node.io.csvwriter.FileWriterSettings
import org.knime.core.data.DataCell
import org.knime.core.data.DataColumnSpec
import org.knime.core.data.DataColumnSpecCreator
import org.knime.core.data.DataRow
import org.knime.core.data.DataTableSpec
import org.knime.core.data.DataType
import org.knime.core.data.RowKey
import org.knime.core.data.StringValue
import org.knime.core.data.`def`.DefaultRow
import org.knime.core.data.`def`.DoubleCell
import org.knime.core.data.`def`.IntCell
import org.knime.core.data.`def`.StringCell
import org.knime.core.node.BufferedDataContainer
import org.knime.core.node.BufferedDataTable
import org.knime.core.node.CanceledExecutionException
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded
import org.knime.core.node.defaultnodesettings.SettingsModelString
import org.knime.core.node.ExecutionContext
import org.knime.core.node.ExecutionMonitor
import org.knime.core.node.InvalidSettingsException
import org.knime.core.node.NodeLogger
import org.knime.core.node.NodeModel
import org.knime.core.node.NodeSettingsRO
import org.knime.core.node.NodeSettingsWO
import org.knime.core.util.FileUtil

import org.knime.data.world.prefs.DWPluginActivator

import scala.collection.JavaConversions.iterableAsScalaIterable


/**
 * This is the model implementation of AddDatasetFile.
 * 
 *
 * @author Appliomics, LLC
 */
class UploadTableToDatasetNodeModel extends NodeModel(1, 0) {
  // NodeModel(1, 0) indicates one incoming port and zero outgoing ports
  
  private val m_datasetURL : SettingsModelString =
    new SettingsModelString(
      UploadTableToDatasetNodeModel.CFGKEY_DATASETURL,
      UploadTableToDatasetNodeModel.DEFAULT_DATASETURL)
  private val m_newFilename : SettingsModelString =
    new SettingsModelString(
      UploadTableToDatasetNodeModel.CFGKEY_NEWFILENAME,
      UploadTableToDatasetNodeModel.DEFAULT_NEWFILENAME)


  /**
   * {@inheritDoc}
   */
  protected override def execute(inData : Array[BufferedDataTable], exec : ExecutionContext) : Array[BufferedDataTable] = {
    val puf = new PushUploadFile()
    
    val inData0 = inData(0)  // Only working with the first input port
    val dataTableSpec = inData0 getDataTableSpec
    
    exec checkCanceled() // Check if the execution monitor was canceled

    // Create a temp local file to hold the table data in csv format
    var desiredFilename = m_newFilename getStringValue()
    if (desiredFilename endsWith(".csv")) desiredFilename = desiredFilename slice(0, desiredFilename.length-4)
    UploadTableToDatasetNodeModel.logger info("DESIRED FILENAME: " + desiredFilename)
    val tempFile = FileUtil.createTempFile(desiredFilename, ".csv", true)
    val currentFilename = tempFile getAbsolutePath
    
    // Write the data table to the temp csv file
    val fileWriterSettings = new FileWriterSettings
    fileWriterSettings setWriteColumnHeader(true)
    val csvWriter = new CSVWriter(
                      new OutputStreamWriter(
                        new BufferedOutputStream(
                          Files newOutputStream(
                            FileUtil resolveToPath(
                              FileUtil toURL(currentFilename)))),
                        Charset defaultCharset),
                      fileWriterSettings)
    csvWriter write(inData0, exec)
    csvWriter close

    // Send the temp csv file to data.world
    UploadTableToDatasetNodeModel.logger info("Preparing to push " + currentFilename + " to data.world")
    val (statusCode, response) = puf.pushSingleFile(extractDatasetNameOnly, currentFilename, desiredFilename + ".csv")
    UploadTableToDatasetNodeModel.logger debug("File pushed to data.world; response msg: " + response.message)

    if (statusCode != 200)
      throw new Exception("Failed to upload " + currentFilename + " to data.world; HTTP status code: " + statusCode + "; response msg: " + response.message)
    UploadTableToDatasetNodeModel.logger info("Interaction with data.world complete")

    // Celebrate success by returning a null
    return null
  }

  /**
   * {@inheritDoc}
   */
  protected override def reset() : Unit = {
    // TODO Code executed on reset.
    // Models build during execute are cleared here.
    // Also data handled in load/saveInternals will be erased here.
  }

  protected def extractDatasetNameOnly() : String = {
    val username : String = DWPluginActivator.getUsername
    val password : String = DWPluginActivator.getAPIKey
    var account : String = username
    var dataset : String = null
    val uri = new URI(m_datasetURL getStringValue)
    val path : Array[String] = uri.getPath.split("/")
    
    if (path.length > 3) {
      UploadTableToDatasetNodeModel.logger debug("Configure sees path.length: " + path.length)
      throw new InvalidSettingsException("URL path is not recognizable; expected: https://data.world/<account>/<dataset>")
    } else if (path.length == 2) {
      account = path(0)
      dataset = path(1)
      if (account == "") {
        UploadTableToDatasetNodeModel.logger debug("Configure sees path.length: " + path.length)
        throw new InvalidSettingsException("URL path is not recognizable; expected: https://data.world/<account>/<dataset>")
      }
    } else {
      UploadTableToDatasetNodeModel.logger debug("Configure saw getHost: " + uri.getHost)
      if (uri.getHost == null) {
        account = username
        dataset = path(0)
      } else {
        if (uri.getHost != "data.world") throw new InvalidSettingsException("URL does not refer to data.world")
        if (uri.getScheme != "https") throw new InvalidSettingsException("URL is expected to use https")
        account = path(1)
        dataset = path(2)
      }
    }
    // We do not use account even if specified, but it provides an opportunity for a self-check.
    if (account != username) throw new InvalidSettingsException("Account name specified does not match user credentials specified in preferences.  Account: " + account + "; user from prefs: " + username)
    UploadTableToDatasetNodeModel.logger debug("Configure using dataset: " + dataset)
    return dataset
  }
  
  /**
   * {@inheritDoc}
   */
  protected override def configure(inSpecs : Array[DataTableSpec]) : Array[DataTableSpec] = {
    val inSpec0 = inSpecs(0)    

    val datasetNameOnly = extractDatasetNameOnly  // Verify that this passes

    return null
  }

  /**
   * {@inheritDoc}
   */
  protected override def saveSettingsTo(settings : NodeSettingsWO) : Unit = {
    m_datasetURL saveSettingsTo(settings)
    m_newFilename saveSettingsTo(settings)
  }

  /**
   * {@inheritDoc}
   */
  protected override def loadValidatedSettingsFrom(settings : NodeSettingsRO) : Unit = {
    m_datasetURL loadSettingsFrom(settings)
    m_newFilename loadSettingsFrom(settings)
  }

  /**
   * {@inheritDoc}
   */
  protected override def validateSettings(settings : NodeSettingsRO) : Unit = {
    m_datasetURL validateSettings(settings)
    m_newFilename validateSettings(settings)
  }
    
  /**
   * {@inheritDoc}
   */
  protected override def loadInternals(internDir : File, exec : ExecutionMonitor) : Unit = {
    // TODO load internal data. 
    // Everything handed to output ports is loaded automatically (data
    // returned by the execute method, models loaded in loadModelContent,
    // and user settings set through loadSettingsFrom - is all taken care 
    // of). Load here only the other internals that need to be restored
    // (e.g. data used by the views).
  }
    
  /**
   * {@inheritDoc}
   */
  protected override def saveInternals(internDir : File, exec : ExecutionMonitor) : Unit = {
    // TODO save internal models. 
    // Everything written to output ports is saved automatically (data
    // returned by the execute method, models saved in the saveModelContent,
    // and user settings saved through saveSettingsTo - is all taken care 
    // of). Save here only the other internals that need to be preserved
    // (e.g. data used by the views).
  }

}

object UploadTableToDatasetNodeModel {
  // the logger instance
  private val logger : NodeLogger = NodeLogger.getLogger(classOf[UploadTableToDatasetNodeModel])

  val CFGKEY_DATASETURL : String = "Dataset URL or Name"
  val DEFAULT_DATASETURL : String = "https://data.world/your_account/dataset-01"

  val CFGKEY_NEWFILENAME : String = "Upload Filename" 
  val DEFAULT_NEWFILENAME : String = "give_me_a_new_unique_name.csv"
  
  val CFGKEY_DATASETNAMEONLY : String = "Dataset Name ONLY"
  val DEFAULT_DATASETNAMEONLY : String = "dataset-01"
}
