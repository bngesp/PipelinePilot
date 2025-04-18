package com.gitlab.pipeline.ui

import com.gitlab.pipeline.model.JobModel
import com.gitlab.pipeline.model.PipelineModel
import com.gitlab.pipeline.service.GitLabApiService
import com.gitlab.pipeline.service.GitLabPipelineService
import com.gitlab.pipeline.settings.GitLabPipelineSettings
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.JBSplitter
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import kotlinx.coroutines.*
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.JPanel
import javax.swing.SwingUtilities

/**
 * Main panel for the GitLab Pipeline tool window.
 */
class GitLabPipelinePanel(
    private val project: Project,
    private val toolWindow: ToolWindow
) : SimpleToolWindowPanel(true, true), Disposable {

    private val pipelineService = project.service<GitLabPipelineService>()
    private val settings = service<GitLabPipelineSettings>()
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val pipelineListPanel = PipelineListPanel(project)
    private val jobDetailsPanel = JobDetailsPanel(project)
    private val logViewerPanel = LogViewerPanel(project)

    private var refreshJob: Job? = null

    init {
        // Set up the UI layout
        val contentPanel = JPanel(BorderLayout())

        // Create a splitter for pipeline list and details
        val mainSplitter = JBSplitter(true, 0.3f)

        // Add pipeline list to the left
        mainSplitter.firstComponent = JBScrollPane(pipelineListPanel)

        // Create a splitter for job details and log viewer
        val detailsSplitter = JBSplitter(true, 0.5f)
        detailsSplitter.firstComponent = JBScrollPane(jobDetailsPanel)
        detailsSplitter.secondComponent = JBScrollPane(logViewerPanel)

        // Add details splitter to the right of the main splitter
        mainSplitter.secondComponent = detailsSplitter

        // Add the main splitter to the content panel
        contentPanel.add(mainSplitter, BorderLayout.CENTER)

        // Add a toolbar at the top
        val toolbarPanel = createToolbarPanel()
        contentPanel.add(toolbarPanel, BorderLayout.NORTH)

        // Set the content panel as the main component
        setContent(contentPanel)

        // Set up listeners
        pipelineListPanel.addPipelineSelectionListener { pipeline ->
            jobDetailsPanel.setPipeline(pipeline)
        }

        jobDetailsPanel.addJobSelectionListener { job ->
            logViewerPanel.setJob(job)
        }

        // Start auto-refresh if enabled
        startAutoRefreshIfEnabled()

        // Initial load of pipelines
        refreshPipelines()
    }

    /**
     * Create the toolbar panel with refresh button and status.
     */
    private fun createToolbarPanel(): JPanel {
        val panel = JPanel(FlowLayout(FlowLayout.LEFT))
        panel.border = JBUI.Borders.empty(2)

        // Add refresh button
        val refreshButton = RefreshButton {
            refreshPipelines()
        }
        panel.add(refreshButton)

        // Add status label
        val statusLabel = JBLabel("Ready")
        panel.add(statusLabel)

        return panel
    }

    /**
     * Refresh the list of pipelines.
     */
    fun refreshPipelines() {
        if (!settings.isConfigured()) {
            showNotConfiguredMessage()
            return
        }

        coroutineScope.launch {
            try {
                val pipelines = pipelineService.getPipelines()
                val pipelineModels = pipelines.map { PipelineModel(it) }

                withContext(Dispatchers.Main) {
                    pipelineListPanel.setPipelines(pipelineModels)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Show error message
                }
            }
        }
    }

    /**
     * Show a message when GitLab API is not configured.
     */
    private fun showNotConfiguredMessage() {
        pipelineListPanel.showMessage("GitLab API not configured. Please set API token in settings.")
    }

    /**
     * Start auto-refresh if enabled in settings.
     */
    private fun startAutoRefreshIfEnabled() {
        if (settings.autoRefreshEnabled) {
            startAutoRefresh(settings.autoRefreshInterval)
        }
    }

    /**
     * Start auto-refresh with the specified interval.
     */
    private fun startAutoRefresh(intervalSeconds: Int) {
        stopAutoRefresh()

        refreshJob = coroutineScope.launch {
            while (isActive) {
                delay(intervalSeconds * 1000L)
                refreshPipelines()
            }
        }
    }

    /**
     * Stop auto-refresh.
     */
    private fun stopAutoRefresh() {
        refreshJob?.cancel()
        refreshJob = null
    }

    override fun dispose() {
        stopAutoRefresh()
        coroutineScope.cancel()
    }

    /**
     * Placeholder classes that will be implemented later.
     */
    private inner class PipelineListPanel(private val project: Project) : JPanel(BorderLayout()) {
        private var selectionListeners = mutableListOf<(PipelineModel) -> Unit>()

        fun setPipelines(pipelines: List<PipelineModel>) {
            // TODO: Implement pipeline list UI
            removeAll()
            add(JBLabel("Pipeline list will be implemented here"), BorderLayout.CENTER)
            revalidate()
            repaint()
        }

        fun showMessage(message: String) {
            removeAll()
            add(JBLabel(message), BorderLayout.CENTER)
            revalidate()
            repaint()
        }

        fun addPipelineSelectionListener(listener: (PipelineModel) -> Unit) {
            selectionListeners.add(listener)
        }
    }

    private inner class JobDetailsPanel(private val project: Project) : JPanel(BorderLayout()) {
        private var selectionListeners = mutableListOf<(JobModel) -> Unit>()

        init {
            add(JBLabel("Job details will be implemented here"), BorderLayout.CENTER)
        }

        fun setPipeline(pipeline: PipelineModel) {
            // TODO: Implement job details UI
        }

        fun addJobSelectionListener(listener: (JobModel) -> Unit) {
            selectionListeners.add(listener)
        }
    }

    private inner class LogViewerPanel(private val project: Project) : JPanel(BorderLayout()) {
        init {
            add(JBLabel("Log viewer will be implemented here"), BorderLayout.CENTER)
        }

        fun setJob(job: JobModel) {
            // TODO: Implement log viewer UI
        }
    }

    private inner class RefreshButton(private val onClick: () -> Unit) : JBLabel("Refresh") {
        init {
            border = JBUI.Borders.empty(4)
            addMouseListener(object : java.awt.event.MouseAdapter() {
                override fun mouseClicked(e: java.awt.event.MouseEvent) {
                    onClick()
                }
            })
        }
    }
}
