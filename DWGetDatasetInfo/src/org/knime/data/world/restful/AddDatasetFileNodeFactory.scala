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
class AddDatasetFileNodeFactory extends NodeFactory[AddDatasetFileNodeModel] {
  
  /**
   * {@inheritDoc}
   */
  override def createNodeModel() : AddDatasetFileNodeModel = new AddDatasetFileNodeModel

  /**
   * {@inheritDoc}
   */
  override def getNrNodeViews() : Int = 0

  /**
   * {@inheritDoc}
   */
  override def createNodeView(viewIndex : Int, nodeModel : AddDatasetFileNodeModel)
    : NodeView[AddDatasetFileNodeModel] = new AddDatasetFileNodeView(nodeModel)

  /**
   * {@inheritDoc}
   */
  override def hasDialog() : Boolean = true

  /**
   * {@inheritDoc}
   */
  override def createNodeDialogPane() : NodeDialogPane = new AddDatasetFileNodeDialog

}
