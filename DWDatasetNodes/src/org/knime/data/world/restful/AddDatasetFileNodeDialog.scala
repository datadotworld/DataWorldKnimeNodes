package org.knime.data.world.restful

import org.knime.core.data.StringValue
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane
import org.knime.core.node.defaultnodesettings.DialogComponentNumber
import org.knime.core.node.defaultnodesettings.DialogComponentString
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded
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
class AddDatasetFileNodeDialog extends DefaultNodeSettingsPane {
  // TODO: Expose option to not overwrite? (Have to check if there first since currently PUT api does overwrite...)
  // TODO: Provide option to specify column containing description/summary of file to also send via api?
  addDialogComponent(
    new DialogComponentString(
      new SettingsModelString(
        AddDatasetFileNodeModel.CFGKEY_DATASETNAME,
        AddDatasetFileNodeModel.DEFAULT_DATASETNAME),
      "Dataset Name:",
      true,
      21))
  addDialogComponent(
    new DialogComponentColumnNameSelection(
      new SettingsModelString(
        AddDatasetFileNodeModel.CFGKEY_COLFILENAME,
        AddDatasetFileNodeModel.DEFAULT_COLFILENAME),
      "Filename:",
      0, // input port 0 provides available columns
      true,
      classOf[StringValue]))
}
