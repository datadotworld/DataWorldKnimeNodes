package org.knime.data.world.prefs

import org.eclipse.ui.plugin.AbstractUIPlugin
import org.knime.core.node.NodeLogger
import org.osgi.framework.BundleContext


class DWPluginActivator extends AbstractUIPlugin {
  DWPluginActivator.logger debug("DWPluginActivator instantiated")

  override def start(context : BundleContext) : Unit = {
    val username : String = DWPluginActivator.getUsername
    val apiKey : String = DWPluginActivator.getAPIKey

    DWPluginActivator.logger info("data.world settings updated for user: " + username)
  }
  
  override def stop(context : BundleContext) : Unit = {
    // Do nothing
  }
}

object DWPluginActivator {
  private val logger : NodeLogger = NodeLogger.getLogger(classOf[DWPluginActivator])
  private var plugin : DWPluginActivator = new DWPluginActivator
  
  // Returns singleton instance of the Plugin
  def getDefault() : DWPluginActivator = { return DWPluginActivator.plugin }
  
  def getUsername() : String = {
    val username : String = DWPluginActivator.plugin getPreferenceStore() getString(DWPreferencePage.P_DW_USERNAME)
    return username    
  }

  def getAPIKey() : String = {
    // TODO: Ensure apiKey is stored encrypted
    val apiKey : String = DWPluginActivator.plugin getPreferenceStore() getString(DWPreferencePage.P_DW_APIKEY)
    return apiKey    
  }
}