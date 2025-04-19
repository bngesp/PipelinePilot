package com.gitlab.pipeline.settings

import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.UI
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel

/**
 * UI component for GitLab Pipeline Manager settings.
 */
class GitLabPipelineSettingsComponent {
    private val gitlabUrlField = JBTextField()
    private val apiTokenField = JBPasswordField()
    private val autoRefreshCheckBox = JBCheckBox("Enable auto-refresh")
    private val refreshIntervalSpinner = JSpinner(SpinnerNumberModel(60, 10, 3600, 10))
    private val showNotificationsCheckBox = JBCheckBox("Show notifications for pipeline status changes")

    val panel: JPanel = FormBuilder.createFormBuilder()
        .addLabeledComponent(JBLabel("GitLab URL:"), gitlabUrlField, 1, false)
        .addLabeledComponent(JBLabel("API Token:"), apiTokenField, 1, false)
        .addComponent(autoRefreshCheckBox, 1)
        .addLabeledComponent(JBLabel("Refresh interval (seconds):"), refreshIntervalSpinner, 1, false)
        .addComponent(showNotificationsCheckBox, 1)
        .addComponentFillVertically(JPanel(), 0)
        .panel
        .apply {
            border = javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)
        }

    init {
        // Add listeners for enabling/disabling refresh interval based on checkbox
        autoRefreshCheckBox.addActionListener {
            refreshIntervalSpinner.isEnabled = autoRefreshCheckBox.isSelected
        }
    }

    var gitlabUrl: String
        get() = gitlabUrlField.text
        set(value) {
            gitlabUrlField.text = value
        }

    var apiToken: String
        get() = String(apiTokenField.password)
        set(value) {
            apiTokenField.text = value
        }

    var autoRefreshEnabled: Boolean
        get() = autoRefreshCheckBox.isSelected
        set(value) {
            autoRefreshCheckBox.isSelected = value
            refreshIntervalSpinner.isEnabled = value
        }

    var autoRefreshInterval: Int
        get() = refreshIntervalSpinner.value as Int
        set(value) {
            refreshIntervalSpinner.value = value
        }

    var showNotifications: Boolean
        get() = showNotificationsCheckBox.isSelected
        set(value) {
            showNotificationsCheckBox.isSelected = value
        }
}
