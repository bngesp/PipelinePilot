package com.gitlab.pipeline.model

import org.gitlab4j.api.models.Pipeline
import org.gitlab4j.api.models.User
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.ZonedDateTime
import java.util.Date

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
            .withStatus("success")
            .withCreatedAt(Date.from(ZonedDateTime.now().minusMinutes(10).toInstant()))
            .withStartedAt(Date.from(ZonedDateTime.now().minusMinutes(9).toInstant()))
            .withFinishedAt(Date.from(ZonedDateTime.now().minusMinutes(5).toInstant()))
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
        assertEquals("success", pipelineModel.status)
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
        pipeline.withStatus("running")
        pipelineModel = PipelineModel(pipeline)
        assertEquals("Running", pipelineModel.getStatusDisplay())

        pipeline.withStatus("failed")
        pipelineModel = PipelineModel(pipeline)
        assertEquals("Failed", pipelineModel.getStatusDisplay())

        pipeline.withStatus("pending")
        pipelineModel = PipelineModel(pipeline)
        assertEquals("Pending", pipelineModel.getStatusDisplay())

        pipeline.withStatus("canceled")
        pipelineModel = PipelineModel(pipeline)
        assertEquals("Canceled", pipelineModel.getStatusDisplay())

        pipeline.withStatus("skipped")
        pipelineModel = PipelineModel(pipeline)
        assertEquals("Skipped", pipelineModel.getStatusDisplay())

        pipeline.withStatus("created")
        pipelineModel = PipelineModel(pipeline)
        assertEquals("Created", pipelineModel.getStatusDisplay())

        pipeline.withStatus("manual")
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
        pipeline.duration = 3665 // 1 hour, 1 minute, 5 seconds
        pipelineModel = PipelineModel(pipeline)
        assertEquals("1:01:05", pipelineModel.getDurationDisplay())

        // Test with no duration
        pipeline.duration = null
        pipelineModel = PipelineModel(pipeline)
        assertEquals("N/A", pipelineModel.getDurationDisplay())
    }

    @Test
    fun testIsFinished() {
        // Test success status (finished)
        assertTrue(pipelineModel.isFinished())

        // Test running status (not finished)
        pipeline.status = "running"
        pipelineModel = PipelineModel(pipeline)
        assertFalse(pipelineModel.isFinished())

        // Test failed status (finished)
        pipeline.status = "failed"
        pipelineModel = PipelineModel(pipeline)
        assertTrue(pipelineModel.isFinished())

        // Test canceled status (finished)
        pipeline.status = "canceled"
        pipelineModel = PipelineModel(pipeline)
        assertTrue(pipelineModel.isFinished())

        // Test skipped status (finished)
        pipeline.status = "skipped"
        pipelineModel = PipelineModel(pipeline)
        assertTrue(pipelineModel.isFinished())

        // Test pending status (not finished)
        pipeline.status = "pending"
        pipelineModel = PipelineModel(pipeline)
        assertFalse(pipelineModel.isFinished())
    }

    @Test
    fun testCanRetry() {
        // Test success status (cannot retry)
        assertFalse(pipelineModel.canRetry())

        // Test failed status (can retry)
        pipeline.status = "failed"
        pipelineModel = PipelineModel(pipeline)
        assertTrue(pipelineModel.canRetry())

        // Test canceled status (can retry)
        pipeline.status = "canceled"
        pipelineModel = PipelineModel(pipeline)
        assertTrue(pipelineModel.canRetry())

        // Test running status (cannot retry)
        pipeline.status = "running"
        pipelineModel = PipelineModel(pipeline)
        assertFalse(pipelineModel.canRetry())
    }

    @Test
    fun testCanCancel() {
        // Test success status (cannot cancel)
        assertFalse(pipelineModel.canCancel())

        // Test running status (can cancel)
        pipeline.status = "running"
        pipelineModel = PipelineModel(pipeline)
        assertTrue(pipelineModel.canCancel())

        // Test pending status (can cancel)
        pipeline.status = "pending"
        pipelineModel = PipelineModel(pipeline)
        assertTrue(pipelineModel.canCancel())

        // Test failed status (cannot cancel)
        pipeline.status = "failed"
        pipelineModel = PipelineModel(pipeline)
        assertFalse(pipelineModel.canCancel())
    }

    @Test
    fun testEquals() {
        // Same ID should be equal
        val pipeline2 = Pipeline()
        pipeline2.id = 123
        val pipelineModel2 = PipelineModel(pipeline2)
        assertEquals(pipelineModel, pipelineModel2)

        // Different ID should not be equal
        val pipeline3 = Pipeline()
        pipeline3.id = 456
        val pipelineModel3 = PipelineModel(pipeline3)
        assertNotEquals(pipelineModel, pipelineModel3)
    }

    @Test
    fun testHashCode() {
        // Same ID should have same hash code
        val pipeline2 = Pipeline()
        pipeline2.id = 123
        val pipelineModel2 = PipelineModel(pipeline2)
        assertEquals(pipelineModel.hashCode(), pipelineModel2.hashCode())

        // Different ID should have different hash code
        val pipeline3 = Pipeline()
        pipeline3.id = 456
        val pipelineModel3 = PipelineModel(pipeline3)
        assertNotEquals(pipelineModel.hashCode(), pipelineModel3.hashCode())
    }
}
