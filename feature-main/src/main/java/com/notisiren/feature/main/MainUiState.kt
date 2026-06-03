package com.notisiren.feature.main

import com.notisiren.shared.domain.model.GmailFilter

data class MainUiState(
    val filters: List<GmailFilter> = emptyList(),
    val isLoading: Boolean = true
)
