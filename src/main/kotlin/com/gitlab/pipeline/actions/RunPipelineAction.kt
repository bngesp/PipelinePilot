package com.gitlab.pipeline.actions

import com.gitlab.pipeline.service.GitLabPipelineService
import com.gitlab.pipeline.settings.GitLabPipelineSettings
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vcs.VcsException
import git4idea.GitUtil
import git4idea.branch.GitBranchUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Action for running a new GitLab pipeline on the current branch.
 */
class RunPipelineAction : AnAction(), DumbAware {
    
    /**
     * Execute the action.
     */
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val settings = service<GitLabPipelineSettings>()
        
        if (!settings.isConfigured()) {
            Messages.showErrorDialog(
                project,
                "GitLab API is not configured. Please set API token in settings.",
                "GitLab Pipeline"
            )
            return
        }
        
        val currentBranch = getCurrentBranch(project)
        if (currentBranch == null) {
            Messages.showErrorDialog(
                project,
                "Failed to determine current Git branch.",
                "GitLab Pipeline"
            )
            return
        }
        
        // Confirm with user
        val confirmation = Messages.showYesNoDialog(
            project,
            "Run a new pipeline on branch '$currentBranch'?",
            "Run GitLab Pipeline",
            Messages.getQuestionIcon()
        )
        
        if (confirmation != Messages.YES) {
            return
        }
        
        // Run pipeline
        val pipelineService = project.service<GitLabPipelineService>()
        
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val pipeline = pipelineService.runPipeline(currentBranch)
                
                withContext(Dispatchers.Main) {
                    if (pipeline != null) {
                        Messages.showInfoMessage(
                            project,
                            "Pipeline #${pipeline.id} started on branch '$currentBranch'.",
                            "GitLab Pipeline"
                        )
                    } else {
                        Messages.showErrorDialog(
                            project,
                            "Failed to start pipeline on branch '$currentBranch'.",
                            "GitLab Pipeline"
                        )
                    }
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    Messages.showErrorDialog(
                        project,
                        "Error starting pipeline: ${ex.message}",
                        "GitLab Pipeline"
                    )
                }
            }
        }
    }
    
    /**
     * Update the action presentation.
     */
    override fun update(e: AnActionEvent) {
        val project = e.project
        e.presentation.isEnabledAndVisible = project != null
    }
    
    /**
     * Get the current Git branch for the project.
     */
    private fun getCurrentBranch(project: Project): String? {
        return try {
            val repository = GitUtil.getRepositoryManager(project).repositories.firstOrNull()
            repository?.let { GitBranchUtil.getCurrentBranch(it)?.name }
        } catch (e: VcsException) {
            null
        }
    }
}