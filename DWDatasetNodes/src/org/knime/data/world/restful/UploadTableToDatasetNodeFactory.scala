package org.knime.data.world.restful

import org.knime.core.node.NodeDialogPane
import org.knime.core.node.NodeFactory
import org.knime.core.node.NodeView

/**
 * <code>NodeFactory</code> for the "AddDatasetFile" Node.
 * 
 *
 * @author Appliomics, LLC
 */
class UploadTableToDatasetNodeFactory extends NodeFactory[UploadTableToDatasetNodeModel] {
  
  /**
   * {@inheritDoc}
   */
  override def createNodeModel() : UploadTableToDatasetNodeModel = new UploadTableToDatasetNodeModel

  /**
   * {@inheritDoc}
   */
  override def getNrNodeViews() : Int = 0

  /**
   * {@inheritDoc}
   */
  override def createNodeView(viewIndex : Int, nodeModel : UploadTableToDatasetNodeModel)
    : NodeView[UploadTableToDatasetNodeModel] = new UploadTableToDatasetNodeView(nodeModel)

  /**
   * {@inheritDoc}
   */
  override def hasDialog() : Boolean = true

  /**
   * {@inheritDoc}
   */
  override def createNodeDialogPane() : NodeDialogPane = new UploadTableToDatasetNodeDialog

}
