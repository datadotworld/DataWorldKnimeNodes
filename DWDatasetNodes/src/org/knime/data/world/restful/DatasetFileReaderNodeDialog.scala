package org.knime.data.world.restful

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane
import org.knime.core.node.defaultnodesettings.DialogComponentColumnFilter
import org.knime.core.node.defaultnodesettings.DialogComponentNumber
import org.knime.core.node.defaultnodesettings.DialogComponentString
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection
import org.knime.core.node.defaultnodesettings.SettingsModelString
import org.knime.core.node.defaultnodesettings.SettingsModelFilterString
import org.knime.base.node.io.database.util.DBReaderDialogPane

import collection.JavaConverters._

/**
 * <code>NodeDialog</code> for the "DatasetFileReader" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Appliomics, LLC
 */
class DatasetFileReaderNodeDialog extends DefaultNodeSettingsPane {
  
  /**
   * New pane for configuring DatasetFileReader node dialog.
   */
  addDialogComponent(
    new DialogComponentString(
      new SettingsModelString(
        DatasetFileReaderNodeModel.CFGKEY_DATASETFILEURL,
        DatasetFileReaderNodeModel.DEFAULT_DATASETFILEURL),
      "Dataset File URL:",
      true,
      72))
  /* TODO: Populate this with options after restful call to look them up?
  addDialogComponent(
    new DialogComponentStringSelection(
      new SettingsModelString(
        DatasetFileReaderNodeModel.CFGKEY_DATASETFILENAME,
        DatasetFileReaderNodeModel.DEFAULT_DATASETFILENAME),
      "File:",
      Set("UNKNOWN").asJava))
  */
  /* TODO: Find example where this can be used without a proper inPort?
  addDialogComponent(
    new DialogComponentColumnFilter(
      new SettingsModelFilterString(
        DatasetFileReaderNodeModel.CFGKEY_DATASETFILECOLS,
        Set("").asJava,
        Set("").asJava,
        true),
      0,
      true)
  */
  
  // TODO: Replace with something appropriate per validate settings methods in NodeModel
  //val sqlStatementSetting = new SettingsModelString(DatasetFileReaderNodeModel.CFGKEY_SQLSTATEMENT, "")

}