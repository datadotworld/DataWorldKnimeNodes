package org.knime.data.world.restful

import java.io.File
import java.net.URI

import org.knime.core.data.DataColumnSpec
import org.knime.core.data.DataColumnSpecCreator
import org.knime.core.data.DataTableSpec

import org.knime.core.node.BufferedDataContainer
import org.knime.core.node.BufferedDataTable
import org.knime.core.node.CanceledExecutionException
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded
import org.knime.core.node.defaultnodesettings.SettingsModelString
import org.knime.core.node.port.database.reader.DBReader
import org.knime.core.node.port.database.DatabaseConnectionSettings
import org.knime.core.node.port.database.DatabaseQueryConnectionSettings
import org.knime.core.node.ExecutionContext
import org.knime.core.node.ExecutionMonitor
import org.knime.core.node.InvalidSettingsException
import org.knime.core.node.NodeLogger
import org.knime.core.node.NodeModel
import org.knime.core.node.NodeSettingsRO
import org.knime.core.node.NodeSettingsWO

import org.knime.core.util.KnimeEncryption

import org.knime.data.world.prefs.DWPluginActivator

import scala.annotation.switch


/**
 * This is the model implementation of GetDatasetInfo.
 * 
 *
 * @author Appliomics, LLC
 */
class DatasetFileReaderNodeModel extends NodeModel(0, 1) {
  // NodeModel(0, 1) indicates zero incoming ports and one outgoing port

  protected val m_settings = new DatabaseQueryConnectionSettings()
  private var m_outSpec : DataTableSpec = null
  private val m_config = new DatasetFileReaderConfiguration


  /**
   * {@inheritDoc}
   */
  protected override def execute(inData : Array[BufferedDataTable], exec : ExecutionContext) : Array[BufferedDataTable] = {
    DatasetFileReaderNodeModel.logger info("Preparing to contact data.world")

    // Reconfigure DataTableSpec for the output port
    val outputSpec : DataTableSpec = configure(null)(0)

    // Populate output port with data
    exec setProgress("Connecting to data.world...")
    val reader = getReader
    val outputDataTable : BufferedDataTable = produceResultTable(exec, reader)
    
    // Update output DataTableSpec
    // TODO: Verify that output spec never actually changes here
    m_outSpec = outputDataTable getDataTableSpec
    
    return Array[BufferedDataTable](outputDataTable)
  }

  protected def getReader() : DBReader = {
    // TODO: populate m_settings as necessary
    val query = m_settings getQuery
    val connSettings = new DatabaseQueryConnectionSettings(m_settings, query)
    val reader = connSettings getUtility() getReader(new DatabaseQueryConnectionSettings(connSettings, query))

    return reader
  }

  protected def produceResultTable(exec : ExecutionContext, reader : DBReader) : BufferedDataTable = {
    val resultDataTable = reader.createTable(exec, getCredentialsProvider)

    return resultDataTable
  }

  /**
   * {@inheritDoc}
   */
  protected override def reset() : Unit = {
    // TODO Code executed on reset.
    // Models build during execute are cleared here.
    // Also data handled in load/saveInternals will be erased here.
  }

  /**
   * {@inheritDoc}
   */
  protected override def configure(inSpecs : Array[DataTableSpec]) : Array[DataTableSpec] = {
    val username : String = DWPluginActivator.getUsername
    val password : String = DWPluginActivator.getAPIKey
    val(account, dataset) = DatasetFileReaderNodeModel.parseDatasetURL(m_config getDatasetString, username)
    DatasetFileReaderNodeModel.logger debug("Configure using account: " + account)
    DatasetFileReaderNodeModel.logger debug("Configure using dataset: " + dataset)
    
    m_settings setDriver("world.data.jdbc.Driver")
    m_settings setJDBCUrl("jdbc:data:world:sql:" + account + ":" + dataset)
    m_settings setUserName(username)  // Currently appears unused/redundant, will keep for now.
    m_settings setPassword(KnimeEncryption.encrypt(password.toArray))
    m_settings setTimezone("none")
    m_settings setAllowSpacesInColumnNames(true)

    val query : String = "SELECT * FROM " + m_config.getTableString
    m_settings setQuery(query)
    
    DatasetFileReaderNodeModel.logger debug("Configure about to determine output data table spec from reader")
    m_outSpec = getReader().getDataTableSpec(getCredentialsProvider)

    return Array[DataTableSpec](m_outSpec)
  }

  @deprecated
  protected def configureFileURL(inSpecs : Array[DataTableSpec]) : Array[DataTableSpec] = {
    val username : String = DWPluginActivator.getUsername
    val password : String = DWPluginActivator.getAPIKey
    val(account, dataset, filename) = DatasetFileReaderNodeModel.parseDatasetFileURL(m_config getDatasetString, username)
    DatasetFileReaderNodeModel.logger debug("Configure using account: " + account)
    DatasetFileReaderNodeModel.logger debug("Configure using dataset: " + dataset)
    DatasetFileReaderNodeModel.logger debug("Configure using filename: " + filename)
    
    m_settings setDriver("world.data.jdbc.Driver")
    m_settings setJDBCUrl("jdbc:data:world:sql:" + account + ":" + dataset)
    m_settings setUserName(username)  // Currently appears unused/redundant, will keep for now.
    m_settings setPassword(KnimeEncryption.encrypt(password.toArray))
    m_settings setTimezone("none")
    m_settings setAllowSpacesInColumnNames(true)

    val query : String = "SELECT * FROM " + filename
    m_settings setQuery(query)
    
    DatasetFileReaderNodeModel.logger debug("Configure about to determine output data table spec from reader")
    m_outSpec = getReader().getDataTableSpec(getCredentialsProvider)

    return Array[DataTableSpec](m_outSpec)
  }

  /**
   * {@inheritDoc}
   */
  protected override def saveSettingsTo(settings : NodeSettingsWO) : Unit = {
    m_config saveConfiguration(settings)
    //m_datasetFileURL saveSettingsTo(settings)
    DatasetFileReaderNodeModel.logger debug("saveSettingsTo about to call saveConnection")
    
    // TODO: Possibly replace with something that helps the related entries re: validated settings below
    //settings.addString(DatasetFileReaderNodeModel.CFGKEY_SQLSTATEMENT, m_settings.getQuery)
    
    m_settings saveConnection(settings)
  }

  /**
   * {@inheritDoc}
   */
  protected override def loadValidatedSettingsFrom(settings : NodeSettingsRO) : Unit = {
    m_config loadSettingsInModel(settings)
    //m_datasetFileURL loadSettingsFrom(settings)

    // TODO: Replace with something that works
    //DatasetFileReaderNodeModel.logger debug("loadValidatedSettingsFrom about to call loadValidatedConnection")
    //val settingsChanged = m_settings loadValidatedConnection(settings, getCredentialsProvider)
    //if (settingsChanged) m_outSpec = null
  }

  /**
   * {@inheritDoc}
   */
  protected override def validateSettings(settings : NodeSettingsRO) : Unit = {
    //m_config validateSettings(settings)  // TODO: Validate dataset looks like url
    //m_datasetFileURL validateSettings(settings)  // TODO: Validate looks like a url
    
    // TODO: Replace with something that works
    //DatasetFileReaderNodeModel.logger debug("validateSettings about to call validateConnection")
    //val connSettings = new DatabaseQueryConnectionSettings
    //connSettings.validateConnection(settings, getCredentialsProvider)
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

object DatasetFileReaderNodeModel {
  // the logger instance
  private val logger : NodeLogger = NodeLogger.getLogger(classOf[DatasetFileReaderNodeModel])

  val CFGKEY_DATASETNAME : String = "Dataset Name"
  val DEFAULT_DATASETNAME : String = "https://data.world/your_account/dataset_name"

  val CFGKEY_TABLENAME : String = "Selected Table"
  val DEFAULT_TABLENAME : String = ""

  val CFGKEY_DATASETFILEURL : String = "Dataset File URL"
  val DEFAULT_DATASETFILEURL : String = "https://data.world/jonloyens/an-intro-to-dataworld-dataset/workspace/file?filename=DataDotWorldBBallStats.csv"

  val CFGKEY_SQLSTATEMENT : String = DatabaseConnectionSettings.CFG_STATEMENT
  
  def parseDatasetURL(datasetURL : String, defaultUsername : String) : (String, String) = {
    // TODO: Refactor UploadTableToDatasetNodeModel to use this utility
    // TODO: Move this utility into a more common location
    var account : String = defaultUsername
    var dataset : String = null
    var filename : String = null
    val uri = new URI(datasetURL)
    val path : Array[String] = uri.getPath.split("/")
    val uriQuery : String = uri.getQuery
    
    if (path.length > 3) {
      throw new InvalidSettingsException("URL path is not recognizable; expected: https://data.world/<account>/<dataset>")
    } else if (path.length == 2) {
      account = path(0)
      dataset = path(1)
      if (account == "") {
        throw new InvalidSettingsException("URL path is not recognizable; expected: https://data.world/<account>/<dataset>")
      }
    } else {
      if (uri.getHost == null) {
        dataset = path(0)
      } else {
        if (uri.getHost != "data.world") throw new InvalidSettingsException("URL does not refer to data.world")
        if (uri.getScheme != "https") throw new InvalidSettingsException("URL is expected to use https")
        account = path(1)
        dataset = path(2)
      }
    }    
    return (account, dataset)
  }

  @deprecated
  def parseDatasetFileURL(datasetURL : String, defaultUsername : String) : (String, String, String) = {
    var account : String = defaultUsername
    var dataset : String = null
    var filename : String = null
    val uri = new URI(datasetURL)
    val path : Array[String] = uri.getPath.split("/")
    val uriQuery : String = uri.getQuery
    
    if (((path.length == 5) && ((path(3) != "workspace") || (path(4) != "file")))
        || (path.length > 5)
        || (path.length == 4)
        || (path.length < 2)) {
      DatasetFileReaderNodeModel.logger debug("Configure sees path.length: " + path.length)
      throw new InvalidSettingsException("URL path is not recognizable; expected: https://data.world/<account>/<dataset>/workspace/file?filename=<datafilename>")
    } else if (path.length == 2) {
      dataset = path(0)
      filename = path(1)
    } else if (path.length == 3) {
      account = path(0)
      dataset = path(1)
      filename = path(2)
    } else {
      if (uri.getHost != "data.world") throw new InvalidSettingsException("URL does not refer to data.world")
      if (uri.getScheme != "https") throw new InvalidSettingsException("URL is expected to use https")
      account = path(1)
      dataset = path(2)
      filename = uriQuery.split('=')(1) // TODO: Verify that filename is key and no other fields
    }
    // Currently, data.world appears to transform filenames containing
    // multiple dots in their names to use underscores (for example,
    // "some.stuff.in.here.csv" is exposed via JDBC as a table instead
    // named "some_stuff_in_here".  We will attempt to follow this
    // potentially undocumented pattern which could of course change.
    if (filename.split('.').length > 1) filename = filename.split('.').dropRight(1).mkString("_")
    
    return (account, dataset, filename)
  }
}
