package com.gitlab.pipeline.actions

import com.gitlab.pipeline.ui.GitLabPipelinePanel
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.wm.ToolWindowManager

/**
 * Action for refreshing the list of GitLab pipelines.
 */
class RefreshPipelinesAction : AnAction(), DumbAware {
    
    /**
     * Execute the action.
     */
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        
        // Find the GitLab Pipeline tool window
        val toolWindowManager = ToolWindowManager.getInstance(project)
        val toolWindow = toolWindowManager.getToolWindow("GitLab Pipelines") ?: return
        
        // Find the GitLabPipelinePanel in the tool window
        val content = toolWindow.contentManager.getContent(0) ?: return
        val pipelinePanel = content.component as? GitLabPipelinePanel
        
        // Refresh the pipelines
        pipelinePanel?.refreshPipelines()
    }
    
    /**
     * Update the action presentation.
     */
    override fun update(e: AnActionEvent) {
        val project = e.project
        e.presentation.isEnabledAndVisible = project != null
    }
}