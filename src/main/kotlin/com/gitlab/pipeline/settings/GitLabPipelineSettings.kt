package com.gitlab.pipeline.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

/**
 * Persistent settings for GitLab Pipeline Manager plugin.
 * Stores GitLab server URL and API token.
 */
@State(
    name = "GitLabPipelineSettings",
    storages = [Storage("gitlab-pipeline-manager.xml")]
)
class GitLabPipelineSettings : PersistentStateComponent<GitLabPipelineSettings> {
    
    var gitlabUrl: String = "https://gitlab.com"
    var apiToken: String = ""
    var autoRefreshEnabled: Boolean = true
    var autoRefreshInterval: Int = 60 // seconds
    var showNotifications: Boolean = true
    
    override fun getState(): GitLabPipelineSettings = this
    
    override fun loadState(state: GitLabPipelineSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }
    
    fun isConfigured(): Boolean {
        return apiToken.isNotBlank()
    }
}