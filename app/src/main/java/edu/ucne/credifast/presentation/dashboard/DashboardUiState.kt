package edu.ucne.credifast.presentation.dashboard

import edu.ucne.credifast.domain.dashboard.model.DashboardData

data class DashboardUiState(
    val data: DashboardData = DashboardData(),
    val isLoading: Boolean = false
)