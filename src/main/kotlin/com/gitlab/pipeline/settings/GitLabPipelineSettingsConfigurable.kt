package com.gitlab.pipeline.settings

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

/**
 * Configurable for GitLab Pipeline Manager settings.
 * Provides UI for configuring GitLab server URL and API token.
 */
class GitLabPipelineSettingsConfigurable : Configurable {
    private var settingsComponent: GitLabPipelineSettingsComponent? = null
    
    override fun getDisplayName(): String = "GitLab Pipeline Manager"
    
    override fun createComponent(): JComponent {
        settingsComponent = GitLabPipelineSettingsComponent()
        return settingsComponent!!.panel
    }
    
    override fun isModified(): Boolean {
        val settings = service<GitLabPipelineSettings>()
        val component = settingsComponent ?: return false
        
        return component.gitlabUrl != settings.gitlabUrl ||
               component.apiToken != settings.apiToken ||
               component.autoRefreshEnabled != settings.autoRefreshEnabled ||
               component.autoRefreshInterval != settings.autoRefreshInterval ||
               component.showNotifications != settings.showNotifications
    }
    
    override fun apply() {
        val settings = service<GitLabPipelineSettings>()
        val component = settingsComponent ?: return
        
        settings.gitlabUrl = component.gitlabUrl
        settings.apiToken = component.apiToken
        settings.autoRefreshEnabled = component.autoRefreshEnabled
        settings.autoRefreshInterval = component.autoRefreshInterval
        settings.showNotifications = component.showNotifications
    }
    
    override fun reset() {
        val settings = service<GitLabPipelineSettings>()
        val component = settingsComponent ?: return
        
        component.gitlabUrl = settings.gitlabUrl
        component.apiToken = settings.apiToken
        component.autoRefreshEnabled = settings.autoRefreshEnabled
        component.autoRefreshInterval = settings.autoRefreshInterval
        component.showNotifications = settings.showNotifications
    }
    
    override fun disposeUIResources() {
        settingsComponent = null
    }
}