package org.knime.data.world.restful

import org.knime.core.node.NodeView

/**
 * <code>NodeView</code> for the "GetDatasetInfo" Node.
 * 
 *
 * @author Appliomics, LLC
 */
class GetDatasetInfoNodeView(nodeModel : GetDatasetInfoNodeModel)
  extends NodeView[GetDatasetInfoNodeModel](nodeModel) {

  /**
   * Constructor creates a new view.
   * 
   * @param nodeModel The model (class: {@link GetDatasetInfoNodeModel})
   */
  // TODO instantiate the components of the view here.

  /**
   * {@inheritDoc}
   */
  protected override def modelChanged() : Unit = {

    // TODO retrieve the new model from your nodemodel and 
    // update the view.
    val nodeModel : GetDatasetInfoNodeModel = getNodeModel
    assert(nodeModel != null)
    
    // be aware of a possibly not executed nodeModel! The data you retrieve
    // from your nodemodel could be null, emtpy, or invalid in any kind.
  }

  /**
   * {@inheritDoc}
   */
  protected override def onClose() : Unit = {
    // TODO things to do when closing the view
  }

  /**
   * {@inheritDoc}
   */
  protected override def onOpen() : Unit = {
    // TODO things to do when opening the view
  }

}
