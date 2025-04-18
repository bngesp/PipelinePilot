package com.gitlab.pipeline.model

import com.intellij.ui.JBColor
import org.gitlab4j.api.models.Pipeline
import java.awt.Color
import java.time.ZonedDateTime
import java.util.Date
import javax.swing.Icon
import javax.swing.ImageIcon

/**
 * UI model for GitLab pipeline.
 * Wraps the GitLab API Pipeline model and provides additional UI-specific functionality.
 */
class PipelineModel(val pipeline: Pipeline) {
    val id: Long = pipeline.id
    val ref: String = pipeline.ref
    val sha: String = pipeline.sha
    val status: String = pipeline.status.toString()
    val createdAt: ZonedDateTime? = pipeline.createdAt?.toInstant()?.atZone(java.time.ZoneId.systemDefault())
    val updatedAt: ZonedDateTime? = pipeline.updatedAt?.toInstant()?.atZone(java.time.ZoneId.systemDefault())
    val startedAt: ZonedDateTime? = pipeline.startedAt?.toInstant()?.atZone(java.time.ZoneId.systemDefault())
    val finishedAt: ZonedDateTime? = pipeline.finishedAt?.toInstant()?.atZone(java.time.ZoneId.systemDefault())
    val duration: Int? = pipeline.duration
    val user: String = pipeline.user?.name ?: "Unknown"

    /**
     * Get a human-readable display name for the pipeline.
     */
    fun getDisplayName(): String {
        return "Pipeline #${pipeline.id} (${pipeline.ref})"
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
     * Get color for the pipeline status.
     */
    fun getStatusColor(): Color {
        return when (status) {
            "success" -> JBColor(Color(0, 150, 0), Color(0, 150, 0))
            "failed" -> JBColor(Color(200, 0, 0), Color(200, 0, 0))
            "running" -> JBColor(Color(0, 100, 200), Color(0, 100, 200))
            "pending" -> JBColor(Color(150, 150, 0), Color(150, 150, 0))
            "canceled" -> JBColor(Color(100, 100, 100), Color(100, 100, 100))
            "skipped" -> JBColor(Color(100, 100, 100), Color(100, 100, 100))
            "manual" -> JBColor(Color(150, 100, 0), Color(150, 100, 0))
            else -> JBColor.GRAY
        }
    }

    /**
     * Get icon for the pipeline status.
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
        val duration = pipeline.duration ?: return "N/A"

        val hours = duration / 3600
        val minutes = (duration % 3600) / 60
        val seconds = duration % 60

        return when {
            hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, seconds)
            else -> String.format("%d:%02d", minutes, seconds)
        }
    }

    /**
     * Check if the pipeline is in a final state.
     */
    fun isFinished(): Boolean {
        return when (status) {
            "success",
            "failed",
            "canceled",
            "skipped" -> true
            else -> false
        }
    }

    /**
     * Check if the pipeline can be retried.
     */
    fun canRetry(): Boolean {
        return status == "failed" || 
               status == "canceled"
    }

    /**
     * Check if the pipeline can be canceled.
     */
    fun canCancel(): Boolean {
        return status == "running" || 
               status == "pending"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PipelineModel

        return pipeline.id == other.pipeline.id
    }

    override fun hashCode(): Int {
        return pipeline.id.toInt()
    }
}
