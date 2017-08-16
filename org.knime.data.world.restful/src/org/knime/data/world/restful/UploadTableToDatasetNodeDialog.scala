package org.knime.data.world.restful

import org.knime.core.data.StringValue
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane
import org.knime.core.node.defaultnodesettings.DialogComponentString
import org.knime.core.node.defaultnodesettings.SettingsModelString

/**
 * <code>NodeDialog</code> for the "AddDatasetFile" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Appliomics, LLC
 */
class UploadTableToDatasetNodeDialog extends DefaultNodeSettingsPane {
  // TODO: Expose option to not overwrite? (Have to check if there first since currently PUT api does overwrite...)
  addDialogComponent(
    new DialogComponentString(
      new SettingsModelString(
        UploadTableToDatasetNodeModel.CFGKEY_DATASETURL,
        UploadTableToDatasetNodeModel.DEFAULT_DATASETURL),
      "Dataset URL or Name:",
      true,
      24))
  addDialogComponent(
    new DialogComponentString(
      new SettingsModelString(
        UploadTableToDatasetNodeModel.CFGKEY_NEWFILENAME,
        UploadTableToDatasetNodeModel.DEFAULT_NEWFILENAME),
      "Name for Uploaded File:",
      true,
      24))
}
