package org.knime.data.world.restful

import org.eclipse.core.runtime.Plugin
import org.osgi.framework.BundleContext

/**
 * This is the eclipse bundle activator.
 * Note: KNIME node developers probably won't have to do anything in here, 
 * as this class is only needed by the eclipse platform/plugin mechanism.
 * If you want to move/rename this file, make sure to change the plugin.xml
 * file in the project root directory accordingly.
 *
 * @author Appliomics, LLC
 */
class GetDatasetInfoNodePlugin extends Plugin {

  GetDatasetInfoNodePlugin.plugin = this  // Set the shared instance to this instance
  
  /**
   * This method is called upon plug-in activation.
   * 
   * @param context The OSGI bundle context
   * @throws Exception If this plugin could not be started
   */
  override def start(context : BundleContext) : Unit = super.start(context)
  
  /**
   * This method is called when the plug-in is stopped.
   * 
   * @param context The OSGI bundle context
   * @throws Exception If this plugin could not be stopped
   */
  override def stop(context : BundleContext) : Unit = {
    super.stop(context)
    GetDatasetInfoNodePlugin.plugin = null
  }
  
  
}

object GetDatasetInfoNodePlugin {
  // The shared plugin instance.
  private var plugin : GetDatasetInfoNodePlugin = null

  /**
   * Returns the shared plugin instance.
   * 
   * @return Singleton instance of the Plugin
   */
  def getDefault() : GetDatasetInfoNodePlugin = plugin
}