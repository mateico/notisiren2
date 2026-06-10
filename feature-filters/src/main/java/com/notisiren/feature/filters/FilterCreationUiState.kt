package com.notisiren.feature.filters

data class FilterCreationUiState(
    val filterName: String = "",
    val senderContains: String = "",
    val subjectContains: String = "",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)

