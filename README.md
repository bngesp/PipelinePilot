# GitLab Pipeline Manager

![Build](https://github.com/username/gitlab-pipeline-manager/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)

<!-- Plugin description -->
**GitLab Pipeline Manager** is an IntelliJ IDEA plugin that allows you to manage GitLab CI/CD pipelines directly from your IDE.

## Features

- **Authentication** with GitLab via API token
- **Pipeline Management**:
  - View list of pipelines for your project
  - See detailed status of pipelines and jobs
  - Start/restart pipelines
  - View real-time execution logs
- **Seamless Integration** with your project's Git context
- **Advanced Filtering** by status, branch, or user
- **Notifications** for pipeline status changes
- **Support** for custom pipeline variables
- **Multi-project** GitLab support

## Getting Started

1. Install the plugin from JetBrains Marketplace
2. Configure your GitLab server URL and API token in the plugin settings
3. Open the GitLab Pipeline tool window to start managing your pipelines

## Requirements

- IntelliJ IDEA 2022.3 or newer
- GitLab instance with API access
<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "GitLab Pipeline Manager"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/username/gitlab-pipeline-manager/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

## Usage

1. Open your project in IntelliJ IDEA
2. Configure the GitLab connection in <kbd>Settings/Preferences</kbd> > <kbd>Tools</kbd> > <kbd>GitLab Pipeline Manager</kbd>
3. Open the GitLab Pipeline tool window using <kbd>View</kbd> > <kbd>Tool Windows</kbd> > <kbd>GitLab Pipelines</kbd>
4. Browse and manage your pipelines directly from the IDE

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.