package org.knime.data.world.restful

import java.io.File

import org.knime.core.data.DataCell
import org.knime.core.data.DataColumnSpec
import org.knime.core.data.DataColumnSpecCreator
import org.knime.core.data.DataRow
import org.knime.core.data.DataTableSpec
import org.knime.core.data.DataType
import org.knime.core.data.RowKey
import org.knime.core.data.`def`.DefaultRow
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


/**
 * This is the model implementation of GetDatasetInfo.
 * 
 *
 * @author Appliomics, LLC
 */
class GetDatasetInfoNodeModel extends NodeModel(0, 2) {
  // NodeModel(0, 2) indicates zero incoming ports and two outgoing ports
  
  private val m_username : SettingsModelString =
    new SettingsModelString(
      GetDatasetInfoNodeModel.CFGKEY_USER,
      GetDatasetInfoNodeModel.DEFAULT_USER)
  private val m_datasetName : SettingsModelString =
    new SettingsModelString(
      GetDatasetInfoNodeModel.CFGKEY_DATASETNAME,
      GetDatasetInfoNodeModel.DEFAULT_DATASETNAME)


  /**
   * {@inheritDoc}
   */
  protected override def execute(inData : Array[BufferedDataTable], exec : ExecutionContext) : Array[BufferedDataTable] = {
    GetDatasetInfoNodeModel.logger info("Preparing to contact data.world")

    // Reconfigure DataTableSpec for each of two output ports
    val outputSpecs : Array[DataTableSpec] = configure(null)
    val outputInfoSpec = outputSpecs(0)
    val outputFilesSpec = outputSpecs(1)

    // Talk to data.world
    val gdi : GetDatasetInfo = new GetDatasetInfo()
    val datasetInfo : DatasetInfo = gdi.request(m_username getStringValue, m_datasetName getStringValue)
    var numFilesInDataset : Int = 0
    try {
      numFilesInDataset = datasetInfo.files length
    } catch {
      case any : Throwable => GetDatasetInfoNodeModel.logger warn("No files found associated with requested Dataset")
    }
    GetDatasetInfoNodeModel.logger info("Interaction with data.world complete")
    
    // Populate first output port with data
    val infoContainer : BufferedDataContainer = exec createDataContainer(outputInfoSpec)
    try {
      val infoCells : Array[DataCell] = Array[DataCell](
        new StringCell(datasetInfo.owner),
        new StringCell(datasetInfo.id),
        new StringCell(datasetInfo.title),
        new StringCell(datasetInfo.description),
        if(datasetInfo.summary != null) new StringCell(datasetInfo.summary) else DataType.getMissingCell,
        if(datasetInfo.tags != null) new StringCell(datasetInfo.tags mkString(", ")) else DataType.getMissingCell,
        if(datasetInfo.license != null) new StringCell(datasetInfo.license) else DataType.getMissingCell,
        new StringCell(datasetInfo.visibility),
        new IntCell(numFilesInDataset),
        new StringCell(datasetInfo.status),
        new StringCell(datasetInfo.created),
        new StringCell(datasetInfo.updated))
      val infoRow : DataRow = new DefaultRow(new RowKey("Row 0"), infoCells : _*)
      infoContainer addRowToTable(infoRow)
    } catch {
      case any : Throwable =>
        throw new Exception("Failed to get data.world Dataset: " + m_username.getStringValue + "/" + m_datasetName.getStringValue)
    }
    infoContainer close
    val outInfo : BufferedDataTable = infoContainer getTable

    // Populate second output port with data
    val filesContainer : BufferedDataContainer = exec createDataContainer(outputFilesSpec)
    for (i <- 0 until numFilesInDataset) {
      val fileInfo : FileInfo = datasetInfo.files(i)
      GetDatasetInfoNodeModel.logger debug("Received file info: " + fileInfo.toString)

      val key : RowKey = new RowKey("Row " + i)
      val cells : Array[DataCell] = Array[DataCell](
        new StringCell(fileInfo.name),
        new IntCell(fileInfo.sizeInBytes),
        if (fileInfo.source != null && fileInfo.source.url != null) new StringCell(fileInfo.source.url) else DataType.getMissingCell,
        if (fileInfo.source != null && fileInfo.source.syncStatus != null) new StringCell(fileInfo.source.syncStatus) else DataType.getMissingCell,
        if (fileInfo.source != null && fileInfo.source.lastSyncStart != null) new StringCell(fileInfo.source.lastSyncStart) else DataType.getMissingCell,
        if (fileInfo.source != null && fileInfo.source.lastSyncSuccess != null) new StringCell(fileInfo.source.lastSyncSuccess) else DataType.getMissingCell,
        if (fileInfo.source != null && fileInfo.source.lastSyncFailure != null) new StringCell(fileInfo.source.lastSyncFailure) else DataType.getMissingCell,
        new StringCell(fileInfo.created),
        new StringCell(fileInfo.updated),
        if (fileInfo.description != null) new StringCell(fileInfo.description) else DataType.getMissingCell,
        if (fileInfo.labels != null) new StringCell(fileInfo.labels mkString(", ")) else DataType.getMissingCell)
      val row : DataRow = new DefaultRow(key, cells : _*)
      filesContainer addRowToTable(row)
      
      // check if the execution monitor was canceled
      exec checkCanceled()
      exec setProgress(i / (numFilesInDataset).toFloat, "Adding row " + i)
    }
    filesContainer close
    val outFiles : BufferedDataTable = filesContainer getTable

    
    return Array[BufferedDataTable](outInfo, outFiles)
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
    // TODO: Leverage reflection against GetDatasetInfo.DatasetInfo if possible here
    // TODO: Make Created/Updated columns into timestamps instead of String
    val infoColSpecs : Array[DataColumnSpec] = Array[DataColumnSpec](
      new DataColumnSpecCreator("Owner", StringCell.TYPE).createSpec,
      new DataColumnSpecCreator("Id", StringCell.TYPE).createSpec,
      new DataColumnSpecCreator("Title", StringCell.TYPE).createSpec,
      new DataColumnSpecCreator("Description", StringCell.TYPE).createSpec,
      new DataColumnSpecCreator("Summary", StringCell.TYPE).createSpec,
      new DataColumnSpecCreator("Tags", StringCell.TYPE).createSpec,
      new DataColumnSpecCreator("License", StringCell.TYPE).createSpec,
      new DataColumnSpecCreator("Visibility", StringCell.TYPE).createSpec,
      new DataColumnSpecCreator("Files Count", IntCell.TYPE).createSpec,
      new DataColumnSpecCreator("Status", StringCell.TYPE).createSpec,
      new DataColumnSpecCreator("Created", StringCell.TYPE).createSpec,
      new DataColumnSpecCreator("Updated", StringCell.TYPE).createSpec)
    val outputInfoSpec : DataTableSpec = new DataTableSpec(infoColSpecs : _*)
    val filesColSpecs : Array[DataColumnSpec] = Array[DataColumnSpec](
      new DataColumnSpecCreator("Name", StringCell.TYPE).createSpec,
      new DataColumnSpecCreator("Size in Bytes", IntCell.TYPE).createSpec,
      new DataColumnSpecCreator("Source Url", StringCell.TYPE).createSpec,
      new DataColumnSpecCreator("Source Sync Status", StringCell.TYPE).createSpec,
      new DataColumnSpecCreator("Source Last Sync Start", StringCell.TYPE).createSpec,
      new DataColumnSpecCreator("Source Last Sync Success", StringCell.TYPE).createSpec,
      new DataColumnSpecCreator("Source Last Sync Failure", StringCell.TYPE).createSpec,
      new DataColumnSpecCreator("Created", StringCell.TYPE).createSpec,
      new DataColumnSpecCreator("Updated", StringCell.TYPE).createSpec,
      new DataColumnSpecCreator("Description", StringCell.TYPE).createSpec,
      new DataColumnSpecCreator("Labels", StringCell.TYPE).createSpec)
    val outputFilesSpec : DataTableSpec = new DataTableSpec(filesColSpecs : _*)

    return Array[DataTableSpec](outputInfoSpec, outputFilesSpec)
  }

  /**
   * {@inheritDoc}
   */
  protected override def saveSettingsTo(settings : NodeSettingsWO) : Unit = {
    m_username saveSettingsTo(settings)
    m_datasetName saveSettingsTo(settings)
  }

  /**
   * {@inheritDoc}
   */
  protected override def loadValidatedSettingsFrom(settings : NodeSettingsRO) : Unit = {
    m_username loadSettingsFrom(settings)
    m_datasetName loadSettingsFrom(settings)
  }

  /**
   * {@inheritDoc}
   */
  protected override def validateSettings(settings : NodeSettingsRO) : Unit = {
    m_username validateSettings(settings)
    m_datasetName validateSettings(settings)
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

object GetDatasetInfoNodeModel {
  // the logger instance
  private val logger : NodeLogger = NodeLogger.getLogger(classOf[GetDatasetInfoNodeModel])

  val CFGKEY_USER : String = "data.world Username"
  val DEFAULT_USER : String = "knime"
  
  val CFGKEY_DATASETNAME : String = "Dataset Name"
  val DEFAULT_DATASETNAME : String = "dataset-01"

}
