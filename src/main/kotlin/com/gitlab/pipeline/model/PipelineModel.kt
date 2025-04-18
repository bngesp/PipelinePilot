package com.gitlab.pipeline.model

import com.intellij.ui.JBColor
import org.gitlab4j.api.Constants.PipelineStatus
import org.gitlab4j.api.models.Pipeline
import java.awt.Color
import java.time.ZonedDateTime
import javax.swing.Icon
import javax.swing.ImageIcon

/**
 * UI model for GitLab pipeline.
 * Wraps the GitLab API Pipeline model and provides additional UI-specific functionality.
 */
class PipelineModel(val pipeline: Pipeline) {
    val id: Int = pipeline.id
    val ref: String = pipeline.ref
    val sha: String = pipeline.sha
    val status: String = pipeline.status
    val createdAt: ZonedDateTime? = pipeline.createdAt
    val updatedAt: ZonedDateTime? = pipeline.updatedAt
    val startedAt: ZonedDateTime? = pipeline.startedAt
    val finishedAt: ZonedDateTime? = pipeline.finishedAt
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
            PipelineStatus.PENDING.toString() -> "Pending"
            PipelineStatus.RUNNING.toString() -> "Running"
            PipelineStatus.SUCCESS.toString() -> "Success"
            PipelineStatus.FAILED.toString() -> "Failed"
            PipelineStatus.CANCELED.toString() -> "Canceled"
            PipelineStatus.SKIPPED.toString() -> "Skipped"
            PipelineStatus.CREATED.toString() -> "Created"
            PipelineStatus.MANUAL.toString() -> "Manual"
            else -> status.capitalize()
        }
    }
    
    /**
     * Get color for the pipeline status.
     */
    fun getStatusColor(): Color {
        return when (status) {
            PipelineStatus.SUCCESS.toString() -> JBColor(Color(0, 150, 0), Color(0, 150, 0))
            PipelineStatus.FAILED.toString() -> JBColor(Color(200, 0, 0), Color(200, 0, 0))
            PipelineStatus.RUNNING.toString() -> JBColor(Color(0, 100, 200), Color(0, 100, 200))
            PipelineStatus.PENDING.toString() -> JBColor(Color(150, 150, 0), Color(150, 150, 0))
            PipelineStatus.CANCELED.toString() -> JBColor(Color(100, 100, 100), Color(100, 100, 100))
            PipelineStatus.SKIPPED.toString() -> JBColor(Color(100, 100, 100), Color(100, 100, 100))
            PipelineStatus.MANUAL.toString() -> JBColor(Color(150, 100, 0), Color(150, 100, 0))
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
            PipelineStatus.SUCCESS.toString(),
            PipelineStatus.FAILED.toString(),
            PipelineStatus.CANCELED.toString(),
            PipelineStatus.SKIPPED.toString() -> true
            else -> false
        }
    }
    
    /**
     * Check if the pipeline can be retried.
     */
    fun canRetry(): Boolean {
        return status == PipelineStatus.FAILED.toString() || 
               status == PipelineStatus.CANCELED.toString()
    }
    
    /**
     * Check if the pipeline can be canceled.
     */
    fun canCancel(): Boolean {
        return status == PipelineStatus.RUNNING.toString() || 
               status == PipelineStatus.PENDING.toString()
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        other as PipelineModel
        
        return pipeline.id == other.pipeline.id
    }
    
    override fun hashCode(): Int {
        return pipeline.id
    }
}