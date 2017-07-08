package org.knime.data.world.restful

import org.knime.core.data.DataTableSpec
import org.knime.core.node.NodeDialogPane
import org.knime.core.node.NodeLogger
import org.knime.core.node.NodeSettingsRO
import org.knime.core.node.NodeSettingsWO
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane
import org.knime.core.node.defaultnodesettings.DialogComponentString
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection
import org.knime.core.node.defaultnodesettings.SettingsModelString
import org.knime.core.node.defaultnodesettings.SettingsModelFilterString
import org.knime.core.node.port.database.DatabaseUtility
import org.knime.core.node.port.database.DatabaseQueryConnectionSettings
import org.knime.core.util.KnimeEncryption

import collection.JavaConverters._

import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.ItemEvent
import java.awt.event.ItemListener
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.sql.DriverManager
import java.sql.ResultSet
import javax.swing.DefaultComboBoxModel
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

import org.knime.data.world.prefs.DWPluginActivator

import scala.collection.mutable.ArrayBuffer


/**
 * <code>NodeDialog</code> for the "DatasetFileReader" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Appliomics, LLC
 */
class DatasetFileReaderNodeDialog extends NodeDialogPane {
  
  /**
   * New pane for configuring DatasetFileReader node dialog.
   */
  val m_datasetField = new JTextField(30)
  val m_tableSelectionModel = new DefaultComboBoxModel[String]
  val m_tableSelection = new JComboBox(m_tableSelectionModel)
  
  val mainPanel = new JPanel(new GridBagLayout)
  val gbc = new GridBagConstraints
  
  gbc.insets = new Insets(5, 5, 5, 5)
  gbc.gridx = 0
  gbc.gridy = 0
  gbc.gridwidth = 1
  gbc.anchor = GridBagConstraints.WEST
  
  mainPanel add(new JLabel("Dataset URL or Name: "), gbc)
  gbc.gridx += 1
  gbc.gridwidth = 2
  mainPanel add(m_datasetField, gbc)
  
  gbc.gridy += 1
  mainPanel add(createJButton("Fetch Available Tables", updateAvailableTables(null)), gbc)
  
  gbc.gridx = 0
  gbc.gridy += 1
  gbc.gridwidth = 1
  mainPanel add(new JLabel("Selected Table: "), gbc)
  gbc.gridx += 1
  gbc.gridwidth = 2
  mainPanel add(m_tableSelection, gbc)
  
  addTab("Table Selection", mainPanel)
  
  protected override def loadSettingsFrom(settings : NodeSettingsRO, specs : Array[DataTableSpec]) : Unit = {
    val config = new DatasetFileReaderConfiguration
    config loadSettingsInDialog(settings)
    m_datasetField setText(config getDatasetString)
    if (m_tableSelection.getItemCount == 0) m_tableSelection addItem(config getTableString)
    m_tableSelection setSelectedItem(config getTableString)
  }
  
  protected override def saveSettingsTo(settings : NodeSettingsWO) : Unit = {
    val config = new DatasetFileReaderConfiguration
    config setDatasetString(m_datasetField getText)
    config setTableString(m_tableSelection getSelectedItem)
    config saveConfiguration(settings)
  }
  
  protected def retrieveAvailableTables(account : String, dataset : String) : Array[String] = {
    DatasetFileReaderNodeDialog.logger debug("Entered retrieveAvailableTables")
    // TODO: Consolidate as much of below as possible with similar work done in NodeModel
    val username : String = DWPluginActivator.getUsername
    val password : String = DWPluginActivator.getAPIKey
    val url = "jdbc:data:world:sql:" + account + ":" + dataset
    val resultBuffer = ArrayBuffer[String]()
    val dbu = DatabaseUtility.getUtility("data:world:sql:" + account + ":" + dataset)
    val dbqcSettings = new DatabaseQueryConnectionSettings()
    val connFactory = dbu.getConnectionFactory()
    dbqcSettings setDriver("world.data.jdbc.Driver")
    dbqcSettings setJDBCUrl("jdbc:data:world:sql:" + account + ":" + dataset)
    dbqcSettings setUserName(username)  // Currently appears unused/redundant, will keep for now.
    dbqcSettings setPassword(KnimeEncryption.encrypt(password.toArray))
    dbqcSettings setTimezone("none")
    dbqcSettings setAllowSpacesInColumnNames(true)
    val conn = connFactory.getConnection(getCredentialsProvider, dbqcSettings)
    val md = conn.getMetaData()
    DatasetFileReaderNodeDialog.logger debug("Got metadata")
    val rs = md.getTables(null, null, "%", null) // TODO: trap if hung up here
    DatasetFileReaderNodeDialog.logger debug("Retrieved available tables")
    while (rs.next) {
      DatasetFileReaderNodeDialog.logger debug("Found table: " + rs.getString(3))
      resultBuffer += rs getString(3)
    }
    return resultBuffer.toArray
  }

  protected def updateAvailableTables(ae : ActionEvent) : Unit = {
    DatasetFileReaderNodeDialog.logger debug("Button clicked")
    val username : String = DWPluginActivator.getUsername
    val(account, dataset) = DatasetFileReaderNodeModel.parseDatasetURL(m_datasetField getText, username)
    m_tableSelectionModel removeAllElements()
    for (tableName <- retrieveAvailableTables(account, dataset)) {
      DatasetFileReaderNodeDialog.logger debug("Adding to ComboBox: " + tableName)
      m_tableSelectionModel addElement(tableName)
    }
  }

  def createJButton(text : String, fun : => Any) = {
    val m_button = new JButton(text)
    m_button addActionListener(new ActionListener {
      override def actionPerformed(ae : ActionEvent) = fun
    })
    m_button
  }

  // TODO: Replace with something appropriate per validate settings methods in NodeModel
  //val sqlStatementSetting = new SettingsModelString(DatasetFileReaderNodeModel.CFGKEY_SQLSTATEMENT, "")

}

object DatasetFileReaderNodeDialog {
  // the logger instance
  private val logger : NodeLogger = NodeLogger.getLogger(classOf[DatasetFileReaderNodeDialog])

}