package org.knime.data.world.restful

import org.knime.core.node.NodeDialogPane
import org.knime.core.node.NodeFactory
import org.knime.core.node.NodeView

/**
 * <code>NodeFactory</code> for the "GetDatasetInfo" Node.
 * 
 *
 * @author Appliomics, LLC
 */
class GetDatasetInfoNodeFactory extends NodeFactory[GetDatasetInfoNodeModel] {
  
  /**
   * {@inheritDoc}
   */
  override def createNodeModel() : GetDatasetInfoNodeModel = new GetDatasetInfoNodeModel

  /**
   * {@inheritDoc}
   */
  override def getNrNodeViews() : Int = 0

  /**
   * {@inheritDoc}
   */
  override def createNodeView(viewIndex : Int, nodeModel : GetDatasetInfoNodeModel)
    : NodeView[GetDatasetInfoNodeModel] = new GetDatasetInfoNodeView(nodeModel)

  /**
   * {@inheritDoc}
   */
  override def hasDialog() : Boolean = true

  /**
   * {@inheritDoc}
   */
  override def createNodeDialogPane() : NodeDialogPane = new GetDatasetInfoNodeDialog

}

