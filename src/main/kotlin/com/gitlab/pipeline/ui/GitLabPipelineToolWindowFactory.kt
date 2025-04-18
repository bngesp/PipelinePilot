package com.gitlab.pipeline.ui

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

/**
 * Factory for creating the GitLab Pipeline tool window.
 */
class GitLabPipelineToolWindowFactory : ToolWindowFactory, DumbAware {
    
    /**
     * Create the tool window content.
     */
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val pipelinePanel = GitLabPipelinePanel(project, toolWindow)
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content = contentFactory.createContent(pipelinePanel, "Pipelines", false)
        toolWindow.contentManager.addContent(content)
    }
    
    /**
     * Called when the tool window is initialized.
     */
    override fun init(toolWindow: ToolWindow) {
        toolWindow.setToHideOnEmptyContent(true)
    }
    
    /**
     * Determine if the tool window should be available.
     * We always return true since the tool window should be available for all projects.
     */
    override fun shouldBeAvailable(project: Project): Boolean = true
}