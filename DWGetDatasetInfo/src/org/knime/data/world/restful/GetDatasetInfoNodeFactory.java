package org.knime.data.world.restful;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "GetDatasetInfo" Node.
 * Gets info on a data.world dataset via the data.world resful api.
 *
 * @author Appliomics, LLC
 */
public class GetDatasetInfoNodeFactory 
        extends NodeFactory<GetDatasetInfoNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public GetDatasetInfoNodeModel createNodeModel() {
        return new GetDatasetInfoNodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<GetDatasetInfoNodeModel> createNodeView(final int viewIndex,
            final GetDatasetInfoNodeModel nodeModel) {
        return new GetDatasetInfoNodeView(nodeModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDialogPane createNodeDialogPane() {
        return new GetDatasetInfoNodeDialog();
    }

}

