package com.gitlab.pipeline.actions

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware

/**
 * Action group for GitLab Pipeline actions.
 * This group contains actions for running, refreshing, and managing pipelines.
 */
class GitLabPipelineActionGroup : ActionGroup(), DumbAware {
    
    /**
     * Get the actions to display in the group.
     */
    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        e ?: return emptyArray()
        
        // Only show actions if we have a valid project
        val project = e.project ?: return emptyArray()
        
        return arrayOf(
            RunPipelineAction(),
            RefreshPipelinesAction()
            // More actions can be added here
        )
    }
    
    /**
     * Determine if the action group should be displayed.
     */
    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }
}