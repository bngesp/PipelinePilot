package com.gitlab.pipeline.service

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.gitlab4j.api.GitLabApiException
import org.gitlab4j.api.models.Job

/**
 * Service for interacting with GitLab pipeline jobs.
 */
@Service(Service.Level.PROJECT)
class GitLabJobService(private val project: Project) {
    private val logger = Logger.getInstance(GitLabJobService::class.java)
    private val gitLabApiService = service<GitLabApiService>()
    private val pipelineService = project.service<GitLabPipelineService>()
    
    /**
     * Get jobs for a specific pipeline.
     */
    suspend fun getJobs(pipelineId: Int): List<Job> = withContext(Dispatchers.IO) {
        try {
            val gitLabApi = gitLabApiService.getGitLabApi() ?: return@withContext emptyList()
            val projectId = pipelineService.getGitLabProjectId() ?: return@withContext emptyList()
            
            gitLabApi.jobApi.getJobsForPipeline(projectId, pipelineId)
        } catch (e: GitLabApiException) {
            logger.error("Failed to get jobs for pipeline $pipelineId", e)
            emptyList()
        } catch (e: Exception) {
            logger.error("Unexpected error getting jobs for pipeline $pipelineId", e)
            emptyList()
        }
    }
    
    /**
     * Get a specific job by ID.
     */
    suspend fun getJob(jobId: Int): Job? = withContext(Dispatchers.IO) {
        try {
            val gitLabApi = gitLabApiService.getGitLabApi() ?: return@withContext null
            val projectId = pipelineService.getGitLabProjectId() ?: return@withContext null
            
            gitLabApi.jobApi.getJob(projectId, jobId)
        } catch (e: GitLabApiException) {
            logger.error("Failed to get job $jobId", e)
            null
        } catch (e: Exception) {
            logger.error("Unexpected error getting job $jobId", e)
            null
        }
    }
    
    /**
     * Get job logs for a specific job.
     */
    suspend fun getJobLog(jobId: Int): String = withContext(Dispatchers.IO) {
        try {
            val gitLabApi = gitLabApiService.getGitLabApi() ?: return@withContext "GitLab API not configured"
            val projectId = pipelineService.getGitLabProjectId() ?: return@withContext "Failed to determine GitLab project ID"
            
            gitLabApi.jobApi.getJobLog(projectId, jobId)
        } catch (e: GitLabApiException) {
            logger.error("Failed to get log for job $jobId", e)
            "Failed to get job log: ${e.message}"
        } catch (e: Exception) {
            logger.error("Unexpected error getting log for job $jobId", e)
            "Unexpected error getting job log: ${e.message}"
        }
    }
    
    /**
     * Retry a failed job.
     */
    suspend fun retryJob(jobId: Int): Job? = withContext(Dispatchers.IO) {
        try {
            val gitLabApi = gitLabApiService.getGitLabApi() ?: return@withContext null
            val projectId = pipelineService.getGitLabProjectId() ?: return@withContext null
            
            gitLabApi.jobApi.retryJob(projectId, jobId)
        } catch (e: GitLabApiException) {
            logger.error("Failed to retry job $jobId", e)
            null
        } catch (e: Exception) {
            logger.error("Unexpected error retrying job $jobId", e)
            null
        }
    }
    
    /**
     * Cancel a running job.
     */
    suspend fun cancelJob(jobId: Int): Job? = withContext(Dispatchers.IO) {
        try {
            val gitLabApi = gitLabApiService.getGitLabApi() ?: return@withContext null
            val projectId = pipelineService.getGitLabProjectId() ?: return@withContext null
            
            gitLabApi.jobApi.cancelJob(projectId, jobId)
        } catch (e: GitLabApiException) {
            logger.error("Failed to cancel job $jobId", e)
            null
        } catch (e: Exception) {
            logger.error("Unexpected error canceling job $jobId", e)
            null
        }
    }
    
    /**
     * Play a manual job.
     */
    suspend fun playJob(jobId: Int): Job? = withContext(Dispatchers.IO) {
        try {
            val gitLabApi = gitLabApiService.getGitLabApi() ?: return@withContext null
            val projectId = pipelineService.getGitLabProjectId() ?: return@withContext null
            
            gitLabApi.jobApi.playJob(projectId, jobId)
        } catch (e: GitLabApiException) {
            logger.error("Failed to play job $jobId", e)
            null
        } catch (e: Exception) {
            logger.error("Unexpected error playing job $jobId", e)
            null
        }
    }
    
    /**
     * Check if a job is in a final state (success, failed, canceled).
     */
    fun isJobFinished(job: Job): Boolean {
        return when (job.status) {
            "success", "failed", "canceled", "skipped" -> true
            else -> false
        }
    }
}