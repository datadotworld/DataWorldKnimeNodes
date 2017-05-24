package org.knime.data.world.restful;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;

/**
 * <code>NodeDialog</code> for the "GetDatasetInfo" Node.
 * Gets info on a data.world dataset via the data.world resful api.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Appliomics, LLC
 */
public class GetDatasetInfoNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring GetDatasetInfo node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected GetDatasetInfoNodeDialog() {
        super();
        
        addDialogComponent(new DialogComponentNumber(
                new SettingsModelIntegerBounded(
                    GetDatasetInfoNodeModel.CFGKEY_COUNT,
                    GetDatasetInfoNodeModel.DEFAULT_COUNT,
                    Integer.MIN_VALUE, Integer.MAX_VALUE),
                    "Counter:", /*step*/ 1, /*componentwidth*/ 5));
                    
    }
}

