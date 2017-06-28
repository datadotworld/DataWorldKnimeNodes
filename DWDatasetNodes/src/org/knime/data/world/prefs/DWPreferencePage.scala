package org.knime.data.world.prefs

import org.eclipse.jface.preference.StringFieldEditor
import org.eclipse.jface.preference.FieldEditorPreferencePage
import org.eclipse.ui.IWorkbench
import org.eclipse.ui.IWorkbenchPreferencePage


class DWPreferencePage extends FieldEditorPreferencePage(FieldEditorPreferencePage.GRID) with IWorkbenchPreferencePage {
  setPreferenceStore(DWPluginActivator getDefault() getPreferenceStore)
  setDescription("Preferences for data.world Integration.")
  var m_username = DWPluginActivator.getUsername
  var m_apiKey = DWPluginActivator.getAPIKey
  
  override def init(workbench : IWorkbench) : Unit = {
    getPreferenceStore() setDefault(DWPreferencePage.P_DW_USERNAME, "your_data_world_username")
    getPreferenceStore() setDefault(DWPreferencePage.P_DW_APIKEY, "get_from_data_world_settings_advanced")
  }
  
  protected override def createFieldEditors() : Unit = {
    addField(new StringFieldEditor(DWPreferencePage.P_DW_USERNAME, "data.world Username", 36, getFieldEditorParent))
    addField(new StringFieldEditor(DWPreferencePage.P_DW_APIKEY, "data.world API Key", 80, getFieldEditorParent))
  }
  
  protected override def performApply() : Unit = {
    super.performOk
  }
  
  override def performOk() : Boolean = {
    val result = super.performOk
    
    // TODO: Verify that username and api key are not empty
    // TODO: Suggest that username be all lower case if it isn't already
    
    return result
  }
}

object DWPreferencePage {
  val P_DW_USERNAME : String = "data.world username"
  val P_DW_APIKEY : String = "data.world apikey"
}