package com.gitlab.pipeline.settings

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GitLabPipelineSettingsTest {
    private lateinit var settings: GitLabPipelineSettings
    
    @Before
    fun setUp() {
        settings = GitLabPipelineSettings()
    }
    
    @Test
    fun testDefaultValues() {
        // Test default values
        assertEquals("https://gitlab.com", settings.gitlabUrl)
        assertEquals("", settings.apiToken)
        assertTrue(settings.autoRefreshEnabled)
        assertEquals(60, settings.autoRefreshInterval)
        assertTrue(settings.showNotifications)
    }
    
    @Test
    fun testIsConfigured_whenApiTokenEmpty_returnsFalse() {
        // Default state - API token is empty
        assertFalse(settings.isConfigured())
    }
    
    @Test
    fun testIsConfigured_whenApiTokenSet_returnsTrue() {
        // Set API token
        settings.apiToken = "test-token"
        assertTrue(settings.isConfigured())
    }
    
    @Test
    fun testGetState_returnsSelf() {
        // getState should return the instance itself
        assertSame(settings, settings.getState())
    }
    
    @Test
    fun testLoadState_copiesValues() {
        // Create a new settings object with different values
        val newSettings = GitLabPipelineSettings()
        newSettings.gitlabUrl = "https://gitlab.example.com"
        newSettings.apiToken = "new-token"
        newSettings.autoRefreshEnabled = false
        newSettings.autoRefreshInterval = 30
        newSettings.showNotifications = false
        
        // Load state from the new settings
        settings.loadState(newSettings)
        
        // Verify values were copied
        assertEquals("https://gitlab.example.com", settings.gitlabUrl)
        assertEquals("new-token", settings.apiToken)
        assertFalse(settings.autoRefreshEnabled)
        assertEquals(30, settings.autoRefreshInterval)
        assertFalse(settings.showNotifications)
    }
}