package org.knime.data.world.restful

import java.io.File

import org.knime.core.data.DataColumnSpec
import org.knime.core.data.DataColumnSpecCreator
import org.knime.core.data.DataTableSpec
import org.knime.core.data.StringValue
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

import scala.collection.JavaConversions.iterableAsScalaIterable


/**
 * This is the model implementation of AddDatasetFile.
 * 
 *
 * @author Appliomics, LLC
 */
class AddDatasetFileNodeModel extends NodeModel(1, 0) {
  // NodeModel(1, 0) indicates one incoming port and zero outgoing ports
  
  private val m_datasetName : SettingsModelString =
    new SettingsModelString(
      AddDatasetFileNodeModel.CFGKEY_DATASETNAME,
      AddDatasetFileNodeModel.DEFAULT_DATASETNAME)
  private val m_colFilename : SettingsModelString =
    new SettingsModelString(
      AddDatasetFileNodeModel.CFGKEY_COLFILENAME,
      AddDatasetFileNodeModel.DEFAULT_COLFILENAME)


  /**
   * {@inheritDoc}
   */
  protected override def execute(inData : Array[BufferedDataTable], exec : ExecutionContext) : Array[BufferedDataTable] = {
    val puf = new PushUploadFile()
    
    val inData0 = inData(0)  // Only working with the first input port
    val dataTableSpec = inData0 getDataTableSpec
    val colPosition : Int = dataTableSpec findColumnIndex(m_colFilename getStringValue)
    
    // Walk the input rows, sending files one-by-one to data.world
    for (row <- inData0) {
      exec checkCanceled() // check if the execution monitor was canceled

      val currentFilename = row.getCell(colPosition).asInstanceOf[StringValue] getStringValue

      AddDatasetFileNodeModel.logger info("Preparing to push " + currentFilename + " to data.world")
      val (statusCode, response) = puf.pushSingleFile(m_datasetName getStringValue, currentFilename)
      AddDatasetFileNodeModel.logger debug("File pushed to data.world; response msg: " + response.message)

      if (statusCode != 200)
        throw new Exception("Failed to upload " + currentFilename + " to data.world; HTTP status code: " + statusCode + "; response msg: " + response.message)
    }
    AddDatasetFileNodeModel.logger info("Interaction with data.world complete")

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

  /**
   * {@inheritDoc}
   */
  protected override def configure(inSpecs : Array[DataTableSpec]) : Array[DataTableSpec] = {
    // Verify that the input port provides at least one String column.
    // Also verify that the selected column is still present on the input port.
    val inSpec0 = inSpecs(0)
    
    var hasStringColumn : Boolean = false
    var hasSelectedColumn : Boolean = false
    for (i <- 0 until inSpec0.getNumColumns) {
      val columnSpec = inSpec0 getColumnSpec(i)
      if (columnSpec.getType.isCompatible(classOf[StringValue])) hasStringColumn = true
      if (columnSpec.getName == m_colFilename.getStringValue) hasSelectedColumn = true
    }

    if (!hasStringColumn)
      throw new InvalidSettingsException("Input table must contain at least one string column to use as filename.")
    if (!hasSelectedColumn)
      throw new InvalidSettingsException("Selected column not found in the current input table; please (re-) configure the node.")
        
    return null
  }

  /**
   * {@inheritDoc}
   */
  protected override def saveSettingsTo(settings : NodeSettingsWO) : Unit = {
    m_datasetName saveSettingsTo(settings)
    m_colFilename saveSettingsTo(settings)
  }

  /**
   * {@inheritDoc}
   */
  protected override def loadValidatedSettingsFrom(settings : NodeSettingsRO) : Unit = {
    m_datasetName loadSettingsFrom(settings)
    m_colFilename loadSettingsFrom(settings)
  }

  /**
   * {@inheritDoc}
   */
  protected override def validateSettings(settings : NodeSettingsRO) : Unit = {
    m_datasetName validateSettings(settings)
    m_colFilename validateSettings(settings)
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

object AddDatasetFileNodeModel {
  // the logger instance
  private val logger : NodeLogger = NodeLogger.getLogger(classOf[AddDatasetFileNodeModel])

  val CFGKEY_DATASETNAME : String = "Dataset Name"
  val DEFAULT_DATASETNAME : String = "dataset-01"

  val CFGKEY_COLFILENAME : String = "Upload Filename" 
  val DEFAULT_COLFILENAME : String = "Location"  // Default column name from List Files node
}
