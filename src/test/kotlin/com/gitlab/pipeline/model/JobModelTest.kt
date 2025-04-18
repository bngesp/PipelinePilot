package com.gitlab.pipeline.model

import org.gitlab4j.api.models.Job
import org.gitlab4j.api.models.Pipeline
import org.gitlab4j.api.models.User
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.ZonedDateTime

class JobModelTest {
    private lateinit var job: Job
    private lateinit var jobModel: JobModel
    
    @Before
    fun setUp() {
        // Create a test Pipeline for the Job
        val pipeline = Pipeline().withId(123)
        
        // Create a test Job
        job = Job()
            .withId(456)
            .withName("build")
            .withStage("test")
            .withStatus("success")
            .withCreatedAt(ZonedDateTime.now().minusMinutes(10))
            .withStartedAt(ZonedDateTime.now().minusMinutes(9))
            .withFinishedAt(ZonedDateTime.now().minusMinutes(5))
            .withDuration(300f) // 5 minutes in seconds
            .withAllowFailure(false)
        
        job.pipeline = pipeline
        
        val user = User().withName("Test User")
        job.user = user
        
        // Create the model
        jobModel = JobModel(job)
    }
    
    @Test
    fun testProperties() {
        // Test that properties are correctly extracted from the Job
        assertEquals(456, jobModel.id)
        assertEquals("build", jobModel.name)
        assertEquals("test", jobModel.stage)
        assertEquals("success", jobModel.status)
        assertEquals(300f, jobModel.duration)
        assertEquals("Test User", jobModel.user)
        assertEquals(123, jobModel.pipelineId)
        assertFalse(jobModel.allowFailure)
    }
    
    @Test
    fun testGetDisplayName() {
        assertEquals("build (test)", jobModel.getDisplayName())
    }
    
    @Test
    fun testGetStatusDisplay() {
        // Test success status
        assertEquals("Success", jobModel.getStatusDisplay())
        
        // Test other statuses
        job.withStatus("running")
        jobModel = JobModel(job)
        assertEquals("Running", jobModel.getStatusDisplay())
        
        job.withStatus("failed")
        jobModel = JobModel(job)
        assertEquals("Failed", jobModel.getStatusDisplay())
        
        job.withStatus("pending")
        jobModel = JobModel(job)
        assertEquals("Pending", jobModel.getStatusDisplay())
        
        job.withStatus("canceled")
        jobModel = JobModel(job)
        assertEquals("Canceled", jobModel.getStatusDisplay())
        
        job.withStatus("skipped")
        jobModel = JobModel(job)
        assertEquals("Skipped", jobModel.getStatusDisplay())
        
        job.withStatus("created")
        jobModel = JobModel(job)
        assertEquals("Created", jobModel.getStatusDisplay())
        
        job.withStatus("manual")
        jobModel = JobModel(job)
        assertEquals("Manual", jobModel.getStatusDisplay())
        
        // Test unknown status
        job.withStatus("unknown")
        jobModel = JobModel(job)
        assertEquals("Unknown", jobModel.getStatusDisplay())
    }
    
    @Test
    fun testGetDurationDisplay() {
        // Test with 5 minutes duration
        assertEquals("5:00", jobModel.getDurationDisplay())
        
        // Test with longer duration
        job.withDuration(3665f) // 1 hour, 1 minute, 5 seconds
        jobModel = JobModel(job)
        assertEquals("1:01:05", jobModel.getDurationDisplay())
        
        // Test with no duration
        job.withDuration(null)
        jobModel = JobModel(job)
        assertEquals("N/A", jobModel.getDurationDisplay())
    }
    
    @Test
    fun testIsFinished() {
        // Test success status (finished)
        assertTrue(jobModel.isFinished())
        
        // Test running status (not finished)
        job.withStatus("running")
        jobModel = JobModel(job)
        assertFalse(jobModel.isFinished())
        
        // Test failed status (finished)
        job.withStatus("failed")
        jobModel = JobModel(job)
        assertTrue(jobModel.isFinished())
        
        // Test canceled status (finished)
        job.withStatus("canceled")
        jobModel = JobModel(job)
        assertTrue(jobModel.isFinished())
        
        // Test skipped status (finished)
        job.withStatus("skipped")
        jobModel = JobModel(job)
        assertTrue(jobModel.isFinished())
        
        // Test pending status (not finished)
        job.withStatus("pending")
        jobModel = JobModel(job)
        assertFalse(jobModel.isFinished())
    }
    
    @Test
    fun testCanRetry() {
        // Test success status (cannot retry)
        assertFalse(jobModel.canRetry())
        
        // Test failed status (can retry)
        job.withStatus("failed")
        jobModel = JobModel(job)
        assertTrue(jobModel.canRetry())
        
        // Test canceled status (can retry)
        job.withStatus("canceled")
        jobModel = JobModel(job)
        assertTrue(jobModel.canRetry())
        
        // Test running status (cannot retry)
        job.withStatus("running")
        jobModel = JobModel(job)
        assertFalse(jobModel.canRetry())
    }
    
    @Test
    fun testCanCancel() {
        // Test success status (cannot cancel)
        assertFalse(jobModel.canCancel())
        
        // Test running status (can cancel)
        job.withStatus("running")
        jobModel = JobModel(job)
        assertTrue(jobModel.canCancel())
        
        // Test pending status (can cancel)
        job.withStatus("pending")
        jobModel = JobModel(job)
        assertTrue(jobModel.canCancel())
        
        // Test failed status (cannot cancel)
        job.withStatus("failed")
        jobModel = JobModel(job)
        assertFalse(jobModel.canCancel())
    }
    
    @Test
    fun testCanPlay() {
        // Test success status (cannot play)
        assertFalse(jobModel.canPlay())
        
        // Test manual status (can play)
        job.withStatus("manual")
        jobModel = JobModel(job)
        assertTrue(jobModel.canPlay())
        
        // Test running status (cannot play)
        job.withStatus("running")
        jobModel = JobModel(job)
        assertFalse(jobModel.canPlay())
    }
    
    @Test
    fun testEquals() {
        // Same ID should be equal
        val job2 = Job().withId(456)
        val jobModel2 = JobModel(job2)
        assertEquals(jobModel, jobModel2)
        
        // Different ID should not be equal
        val job3 = Job().withId(789)
        val jobModel3 = JobModel(job3)
        assertNotEquals(jobModel, jobModel3)
    }
    
    @Test
    fun testHashCode() {
        // Same ID should have same hash code
        val job2 = Job().withId(456)
        val jobModel2 = JobModel(job2)
        assertEquals(jobModel.hashCode(), jobModel2.hashCode())
        
        // Different ID should have different hash code
        val job3 = Job().withId(789)
        val jobModel3 = JobModel(job3)
        assertNotEquals(jobModel.hashCode(), jobModel3.hashCode())
    }
}