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
class DatasetFileReaderNodeFactory extends NodeFactory[DatasetFileReaderNodeModel] {
  
  /**
   * {@inheritDoc}
   */
  override def createNodeModel() : DatasetFileReaderNodeModel = new DatasetFileReaderNodeModel

  /**
   * {@inheritDoc}
   */
  override def getNrNodeViews() : Int = 0

  /**
   * {@inheritDoc}
   */
  override def createNodeView(viewIndex : Int, nodeModel : DatasetFileReaderNodeModel)
    : NodeView[DatasetFileReaderNodeModel] = new DatasetFileReaderNodeView(nodeModel)

  /**
   * {@inheritDoc}
   */
  override def hasDialog() : Boolean = true

  /**
   * {@inheritDoc}
   */
  override def createNodeDialogPane() : NodeDialogPane = new DatasetFileReaderNodeDialog

}
