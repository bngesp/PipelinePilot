package com.gitlab.pipeline.service

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.gitlab4j.api.Constants.PipelineStatus
import org.gitlab4j.api.GitLabApiException
import org.gitlab4j.api.models.Pipeline
import org.gitlab4j.api.models.ProjectFilter

/**
 * Service for interacting with GitLab pipelines.
 */
@Service(Service.Level.PROJECT)
class GitLabPipelineService(private val project: Project) {
    private val logger = Logger.getInstance(GitLabPipelineService::class.java)
    private val gitLabApiService = service<GitLabApiService>()
    
    /**
     * Get GitLab project ID for the current project.
     * This tries to determine the GitLab project based on the remote URL.
     */
    suspend fun getGitLabProjectId(): Int? = withContext(Dispatchers.IO) {
        try {
            val gitLabApi = gitLabApiService.getGitLabApi() ?: return@withContext null
            
            // TODO: Implement logic to determine GitLab project ID from Git remote URL
            // For now, we'll use a placeholder implementation
            val remoteUrl = "https://gitlab.com/username/project.git" // This should be extracted from Git config
            
            // Extract project path from remote URL
            val projectPath = remoteUrl
                .replace(".git", "")
                .replace("https://gitlab.com/", "")
            
            // Search for project by path
            val projects = gitLabApi.projectApi.getProjects(ProjectFilter().withSearch(projectPath))
            
            projects.firstOrNull { it.pathWithNamespace == projectPath }?.id
        } catch (e: Exception) {
            logger.error("Failed to determine GitLab project ID", e)
            null
        }
    }
    
    /**
     * Get pipelines for the current project.
     */
    suspend fun getPipelines(limit: Int = 20): List<Pipeline> = withContext(Dispatchers.IO) {
        try {
            val gitLabApi = gitLabApiService.getGitLabApi() ?: return@withContext emptyList()
            val projectId = getGitLabProjectId() ?: return@withContext emptyList()
            
            gitLabApi.pipelineApi.getPipelines(projectId, null, null, null, limit)
        } catch (e: GitLabApiException) {
            logger.error("Failed to get pipelines", e)
            emptyList()
        } catch (e: Exception) {
            logger.error("Unexpected error getting pipelines", e)
            emptyList()
        }
    }
    
    /**
     * Get a specific pipeline by ID.
     */
    suspend fun getPipeline(pipelineId: Int): Pipeline? = withContext(Dispatchers.IO) {
        try {
            val gitLabApi = gitLabApiService.getGitLabApi() ?: return@withContext null
            val projectId = getGitLabProjectId() ?: return@withContext null
            
            gitLabApi.pipelineApi.getPipeline(projectId, pipelineId)
        } catch (e: GitLabApiException) {
            logger.error("Failed to get pipeline $pipelineId", e)
            null
        } catch (e: Exception) {
            logger.error("Unexpected error getting pipeline $pipelineId", e)
            null
        }
    }
    
    /**
     * Run a new pipeline on the specified branch.
     */
    suspend fun runPipeline(branch: String): Pipeline? = withContext(Dispatchers.IO) {
        try {
            val gitLabApi = gitLabApiService.getGitLabApi() ?: return@withContext null
            val projectId = getGitLabProjectId() ?: return@withContext null
            
            gitLabApi.pipelineApi.createPipeline(projectId, branch)
        } catch (e: GitLabApiException) {
            logger.error("Failed to run pipeline on branch $branch", e)
            null
        } catch (e: Exception) {
            logger.error("Unexpected error running pipeline on branch $branch", e)
            null
        }
    }
    
    /**
     * Retry a failed pipeline.
     */
    suspend fun retryPipeline(pipelineId: Int): Pipeline? = withContext(Dispatchers.IO) {
        try {
            val gitLabApi = gitLabApiService.getGitLabApi() ?: return@withContext null
            val projectId = getGitLabProjectId() ?: return@withContext null
            
            gitLabApi.pipelineApi.retryPipelineJob(projectId, pipelineId)
            getPipeline(pipelineId)
        } catch (e: GitLabApiException) {
            logger.error("Failed to retry pipeline $pipelineId", e)
            null
        } catch (e: Exception) {
            logger.error("Unexpected error retrying pipeline $pipelineId", e)
            null
        }
    }
    
    /**
     * Cancel a running pipeline.
     */
    suspend fun cancelPipeline(pipelineId: Int): Pipeline? = withContext(Dispatchers.IO) {
        try {
            val gitLabApi = gitLabApiService.getGitLabApi() ?: return@withContext null
            val projectId = getGitLabProjectId() ?: return@withContext null
            
            gitLabApi.pipelineApi.cancelPipelineJobs(projectId, pipelineId)
            getPipeline(pipelineId)
        } catch (e: GitLabApiException) {
            logger.error("Failed to cancel pipeline $pipelineId", e)
            null
        } catch (e: Exception) {
            logger.error("Unexpected error canceling pipeline $pipelineId", e)
            null
        }
    }
    
    /**
     * Check if a pipeline is in a final state (completed, failed, canceled).
     */
    fun isPipelineFinished(pipeline: Pipeline): Boolean {
        return when (pipeline.status) {
            PipelineStatus.SUCCESS.toString(),
            PipelineStatus.FAILED.toString(),
            PipelineStatus.CANCELED.toString(),
            PipelineStatus.SKIPPED.toString() -> true
            else -> false
        }
    }
}