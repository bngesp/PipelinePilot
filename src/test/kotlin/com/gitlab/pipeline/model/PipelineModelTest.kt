package com.gitlab.pipeline.model

import org.gitlab4j.api.Constants.PipelineStatus
import org.gitlab4j.api.models.Pipeline
import org.gitlab4j.api.models.User
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.ZonedDateTime

class PipelineModelTest {
    private lateinit var pipeline: Pipeline
    private lateinit var pipelineModel: PipelineModel
    
    @Before
    fun setUp() {
        // Create a test Pipeline
        pipeline = Pipeline()
            .withId(123)
            .withRef("main")
            .withSha("abc123")
            .withStatus(PipelineStatus.SUCCESS.toString())
            .withCreatedAt(ZonedDateTime.now().minusMinutes(10))
            .withStartedAt(ZonedDateTime.now().minusMinutes(9))
            .withFinishedAt(ZonedDateTime.now().minusMinutes(5))
            .withDuration(300) // 5 minutes in seconds
        
        val user = User().withName("Test User")
        pipeline.user = user
        
        // Create the model
        pipelineModel = PipelineModel(pipeline)
    }
    
    @Test
    fun testProperties() {
        // Test that properties are correctly extracted from the Pipeline
        assertEquals(123, pipelineModel.id)
        assertEquals("main", pipelineModel.ref)
        assertEquals("abc123", pipelineModel.sha)
        assertEquals(PipelineStatus.SUCCESS.toString(), pipelineModel.status)
        assertEquals(300, pipelineModel.duration)
        assertEquals("Test User", pipelineModel.user)
    }
    
    @Test
    fun testGetDisplayName() {
        assertEquals("Pipeline #123 (main)", pipelineModel.getDisplayName())
    }
    
    @Test
    fun testGetStatusDisplay() {
        // Test success status
        assertEquals("Success", pipelineModel.getStatusDisplay())
        
        // Test other statuses
        pipeline.withStatus(PipelineStatus.RUNNING.toString())
        pipelineModel = PipelineModel(pipeline)
        assertEquals("Running", pipelineModel.getStatusDisplay())
        
        pipeline.withStatus(PipelineStatus.FAILED.toString())
        pipelineModel = PipelineModel(pipeline)
        assertEquals("Failed", pipelineModel.getStatusDisplay())
        
        pipeline.withStatus(PipelineStatus.PENDING.toString())
        pipelineModel = PipelineModel(pipeline)
        assertEquals("Pending", pipelineModel.getStatusDisplay())
        
        pipeline.withStatus(PipelineStatus.CANCELED.toString())
        pipelineModel = PipelineModel(pipeline)
        assertEquals("Canceled", pipelineModel.getStatusDisplay())
        
        pipeline.withStatus(PipelineStatus.SKIPPED.toString())
        pipelineModel = PipelineModel(pipeline)
        assertEquals("Skipped", pipelineModel.getStatusDisplay())
        
        pipeline.withStatus(PipelineStatus.CREATED.toString())
        pipelineModel = PipelineModel(pipeline)
        assertEquals("Created", pipelineModel.getStatusDisplay())
        
        pipeline.withStatus(PipelineStatus.MANUAL.toString())
        pipelineModel = PipelineModel(pipeline)
        assertEquals("Manual", pipelineModel.getStatusDisplay())
        
        // Test unknown status
        pipeline.withStatus("unknown")
        pipelineModel = PipelineModel(pipeline)
        assertEquals("Unknown", pipelineModel.getStatusDisplay())
    }
    
    @Test
    fun testGetDurationDisplay() {
        // Test with 5 minutes duration
        assertEquals("5:00", pipelineModel.getDurationDisplay())
        
        // Test with longer duration
        pipeline.withDuration(3665) // 1 hour, 1 minute, 5 seconds
        pipelineModel = PipelineModel(pipeline)
        assertEquals("1:01:05", pipelineModel.getDurationDisplay())
        
        // Test with no duration
        pipeline.withDuration(null)
        pipelineModel = PipelineModel(pipeline)
        assertEquals("N/A", pipelineModel.getDurationDisplay())
    }
    
    @Test
    fun testIsFinished() {
        // Test success status (finished)
        assertTrue(pipelineModel.isFinished())
        
        // Test running status (not finished)
        pipeline.withStatus(PipelineStatus.RUNNING.toString())
        pipelineModel = PipelineModel(pipeline)
        assertFalse(pipelineModel.isFinished())
        
        // Test failed status (finished)
        pipeline.withStatus(PipelineStatus.FAILED.toString())
        pipelineModel = PipelineModel(pipeline)
        assertTrue(pipelineModel.isFinished())
        
        // Test canceled status (finished)
        pipeline.withStatus(PipelineStatus.CANCELED.toString())
        pipelineModel = PipelineModel(pipeline)
        assertTrue(pipelineModel.isFinished())
        
        // Test skipped status (finished)
        pipeline.withStatus(PipelineStatus.SKIPPED.toString())
        pipelineModel = PipelineModel(pipeline)
        assertTrue(pipelineModel.isFinished())
        
        // Test pending status (not finished)
        pipeline.withStatus(PipelineStatus.PENDING.toString())
        pipelineModel = PipelineModel(pipeline)
        assertFalse(pipelineModel.isFinished())
    }
    
    @Test
    fun testCanRetry() {
        // Test success status (cannot retry)
        assertFalse(pipelineModel.canRetry())
        
        // Test failed status (can retry)
        pipeline.withStatus(PipelineStatus.FAILED.toString())
        pipelineModel = PipelineModel(pipeline)
        assertTrue(pipelineModel.canRetry())
        
        // Test canceled status (can retry)
        pipeline.withStatus(PipelineStatus.CANCELED.toString())
        pipelineModel = PipelineModel(pipeline)
        assertTrue(pipelineModel.canRetry())
        
        // Test running status (cannot retry)
        pipeline.withStatus(PipelineStatus.RUNNING.toString())
        pipelineModel = PipelineModel(pipeline)
        assertFalse(pipelineModel.canRetry())
    }
    
    @Test
    fun testCanCancel() {
        // Test success status (cannot cancel)
        assertFalse(pipelineModel.canCancel())
        
        // Test running status (can cancel)
        pipeline.withStatus(PipelineStatus.RUNNING.toString())
        pipelineModel = PipelineModel(pipeline)
        assertTrue(pipelineModel.canCancel())
        
        // Test pending status (can cancel)
        pipeline.withStatus(PipelineStatus.PENDING.toString())
        pipelineModel = PipelineModel(pipeline)
        assertTrue(pipelineModel.canCancel())
        
        // Test failed status (cannot cancel)
        pipeline.withStatus(PipelineStatus.FAILED.toString())
        pipelineModel = PipelineModel(pipeline)
        assertFalse(pipelineModel.canCancel())
    }
    
    @Test
    fun testEquals() {
        // Same ID should be equal
        val pipeline2 = Pipeline().withId(123)
        val pipelineModel2 = PipelineModel(pipeline2)
        assertEquals(pipelineModel, pipelineModel2)
        
        // Different ID should not be equal
        val pipeline3 = Pipeline().withId(456)
        val pipelineModel3 = PipelineModel(pipeline3)
        assertNotEquals(pipelineModel, pipelineModel3)
    }
    
    @Test
    fun testHashCode() {
        // Same ID should have same hash code
        val pipeline2 = Pipeline().withId(123)
        val pipelineModel2 = PipelineModel(pipeline2)
        assertEquals(pipelineModel.hashCode(), pipelineModel2.hashCode())
        
        // Different ID should have different hash code
        val pipeline3 = Pipeline().withId(456)
        val pipelineModel3 = PipelineModel(pipeline3)
        assertNotEquals(pipelineModel.hashCode(), pipelineModel3.hashCode())
    }
}