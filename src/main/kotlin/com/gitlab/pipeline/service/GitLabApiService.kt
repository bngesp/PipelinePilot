package com.gitlab.pipeline.service

import com.gitlab.pipeline.settings.GitLabPipelineSettings
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import org.gitlab4j.api.GitLabApi
import org.gitlab4j.api.GitLabApiException
import java.util.concurrent.ConcurrentHashMap

/**
 * Service for interacting with GitLab API.
 * Handles authentication and provides access to GitLab API client.
 */
@Service
class GitLabApiService {
    private val logger = Logger.getInstance(GitLabApiService::class.java)
    private val gitLabApiCache = ConcurrentHashMap<String, GitLabApi>()

    /**
     * Get a GitLabApi instance for the given project.
     * Uses cached instance if available and settings haven't changed.
     */
    fun getGitLabApi(): GitLabApi? {
        val settings = service<GitLabPipelineSettings>()

        if (!settings.isConfigured()) {
            logger.info("GitLab API not configured. Please set API token in settings.")
            return null
        }

        val cacheKey = "${settings.gitlabUrl}:${settings.apiToken}"

        if (!gitLabApiCache.containsKey(cacheKey)) {
            try {
                logger.info("Creating new GitLabApi instance for ${settings.gitlabUrl}")
                gitLabApiCache[cacheKey] = GitLabApi(settings.gitlabUrl, settings.apiToken)
            } catch (e: Exception) {
                logger.error("Failed to create GitLabApi instance", e)
                return null
            }
        }

        return gitLabApiCache[cacheKey]
    }

    /**
     * Test connection to GitLab API.
     * @return Pair of success flag and message
     */
    fun testConnection(): Pair<Boolean, String> {
        val gitLabApi = getGitLabApi() ?: return Pair(false, "GitLab API not configured")

        return try {
            // Try to get current user to verify connection
            val user = gitLabApi.userApi.currentUser
            Pair(true, "Successfully connected to GitLab as ${user.name}")
        } catch (e: GitLabApiException) {
            logger.error("Failed to connect to GitLab API", e)
            Pair(false, "Failed to connect to GitLab: ${e.message}")
        } catch (e: Exception) {
            logger.error("Unexpected error connecting to GitLab API", e)
            Pair(false, "Unexpected error: ${e.message}")
        }
    }

    /**
     * Clear the GitLabApi cache.
     * Should be called when settings change.
     */
    fun clearCache() {
        gitLabApiCache.clear()
    }
}
