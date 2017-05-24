package org.knime.data.world.restful;

import org.knime.core.node.NodeView;

/**
 * <code>NodeView</code> for the "GetDatasetInfo" Node.
 * Gets info on a data.world dataset via the data.world resful api.
 *
 * @author Appliomics, LLC
 */
public class GetDatasetInfoNodeView extends NodeView<GetDatasetInfoNodeModel> {

    /**
     * Creates a new view.
     * 
     * @param nodeModel The model (class: {@link GetDatasetInfoNodeModel})
     */
    protected GetDatasetInfoNodeView(final GetDatasetInfoNodeModel nodeModel) {
        super(nodeModel);

        // TODO instantiate the components of the view here.

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void modelChanged() {

        // TODO retrieve the new model from your nodemodel and 
        // update the view.
        GetDatasetInfoNodeModel nodeModel = 
            (GetDatasetInfoNodeModel)getNodeModel();
        assert nodeModel != null;
        
        // be aware of a possibly not executed nodeModel! The data you retrieve
        // from your nodemodel could be null, emtpy, or invalid in any kind.
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onClose() {
    
        // TODO things to do when closing the view
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onOpen() {

        // TODO things to do when opening the view
    }

}

