package com.gitlab.pipeline.model

import com.intellij.ui.JBColor
import org.gitlab4j.api.models.Job
import java.awt.Color
import java.time.ZonedDateTime
import java.util.Date
import javax.swing.Icon

/**
 * UI model for GitLab pipeline job.
 * Wraps the GitLab API Job model and provides additional UI-specific functionality.
 */
class JobModel(val job: Job) {
    val id: Long = job.id
    val name: String = job.name
    val stage: String = job.stage
    val status: String = job.status.toString()
    val createdAt: ZonedDateTime? = job.createdAt?.toInstant()?.atZone(java.time.ZoneId.systemDefault())
    val startedAt: ZonedDateTime? = job.startedAt?.toInstant()?.atZone(java.time.ZoneId.systemDefault())
    val finishedAt: ZonedDateTime? = job.finishedAt?.toInstant()?.atZone(java.time.ZoneId.systemDefault())
    val duration: Float? = job.duration
    val user: String = job.user?.name ?: "Unknown"
    val pipelineId: Long = job.pipeline.id
    val allowFailure: Boolean = job.allowFailure

    /**
     * Get a human-readable display name for the job.
     */
    fun getDisplayName(): String {
        return "${job.name} (${job.stage})"
    }

    /**
     * Get a human-readable status description.
     */
    fun getStatusDisplay(): String {
        return when (status) {
            "pending" -> "Pending"
            "running" -> "Running"
            "success" -> "Success"
            "failed" -> "Failed"
            "canceled" -> "Canceled"
            "skipped" -> "Skipped"
            "created" -> "Created"
            "manual" -> "Manual"
            else -> status.replaceFirstChar { it.uppercase() }
        }
    }

    /**
     * Get color for the job status.
     */
    fun getStatusColor(): Color {
        return when (status) {
            "success" -> JBColor(Color(0, 150, 0), Color(0, 150, 0))
            "failed" -> if (allowFailure) JBColor(Color(200, 100, 0), Color(200, 100, 0)) else JBColor(Color(200, 0, 0), Color(200, 0, 0))
            "running" -> JBColor(Color(0, 100, 200), Color(0, 100, 200))
            "pending" -> JBColor(Color(150, 150, 0), Color(150, 150, 0))
            "canceled" -> JBColor(Color(100, 100, 100), Color(100, 100, 100))
            "skipped" -> JBColor(Color(100, 100, 100), Color(100, 100, 100))
            "manual" -> JBColor(Color(150, 100, 0), Color(150, 100, 0))
            else -> JBColor.GRAY
        }
    }

    /**
     * Get icon for the job status.
     * Note: In a real implementation, these would be actual icons from resources.
     */
    fun getStatusIcon(): Icon? {
        // This is a placeholder. In a real implementation, we would load actual icons.
        // For now, we'll return null and let the UI handle it with text-based status.
        return null
    }

    /**
     * Get a human-readable duration string.
     */
    fun getDurationDisplay(): String {
        val duration = job.duration?.toInt() ?: return "N/A"

        val hours = duration / 3600
        val minutes = (duration % 3600) / 60
        val seconds = duration % 60

        return when {
            hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, seconds)
            else -> String.format("%d:%02d", minutes, seconds)
        }
    }

    /**
     * Check if the job is in a final state.
     */
    fun isFinished(): Boolean {
        return when (status) {
            "success", "failed", "canceled", "skipped" -> true
            else -> false
        }
    }

    /**
     * Check if the job can be retried.
     */
    fun canRetry(): Boolean {
        return status == "failed" || status == "canceled"
    }

    /**
     * Check if the job can be canceled.
     */
    fun canCancel(): Boolean {
        return status == "running" || status == "pending"
    }

    /**
     * Check if the job can be manually started.
     */
    fun canPlay(): Boolean {
        return status == "manual"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JobModel

        return job.id == other.job.id
    }

    override fun hashCode(): Int {
        return job.id.toInt()
    }
}
