package org.knime.data.world.restful

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane
import org.knime.core.node.defaultnodesettings.DialogComponentNumber
import org.knime.core.node.defaultnodesettings.DialogComponentString
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded
import org.knime.core.node.defaultnodesettings.SettingsModelString

/**
 * <code>NodeDialog</code> for the "GetDatasetInfo" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Appliomics, LLC
 */
class GetDatasetInfoNodeDialog extends DefaultNodeSettingsPane {
  
  /**
   * New pane for configuring GetDatasetInfo node dialog.
   */
  // TODO: Also support supplying a url instead of these fragments of user/dataset
  addDialogComponent(
    new DialogComponentString(
      new SettingsModelString(
        GetDatasetInfoNodeModel.CFGKEY_USER,
        GetDatasetInfoNodeModel.DEFAULT_USER),
      "data.world Username:",
      true,
      36));
  addDialogComponent(
    new DialogComponentString(
      new SettingsModelString(
        GetDatasetInfoNodeModel.CFGKEY_DATASETNAME,
        GetDatasetInfoNodeModel.DEFAULT_DATASETNAME),
      "Dataset Name:",
      true,
      36));
}