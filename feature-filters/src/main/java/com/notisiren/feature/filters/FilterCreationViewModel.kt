package com.notisiren.feature.filters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notisiren.shared.domain.model.GmailFilter
import com.notisiren.shared.domain.usecase.CreateFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class FilterCreationViewModel @Inject constructor(
    private val createFilter: CreateFilter
) : ViewModel() {

    private val _uiState = MutableStateFlow(FilterCreationUiState())
    val uiState = _uiState.asStateFlow()

    fun onFilterNameChanged(value: String) {
        _uiState.update { it.copy(filterName = value, errorMessage = null) }
    }

    fun onSenderContainsChanged(value: String) {
        _uiState.update { it.copy(senderContains = value, errorMessage = null) }
    }

    fun onSubjectContainsChanged(value: String) {
        _uiState.update { it.copy(subjectContains = value, errorMessage = null) }
    }

    fun onSaveFilter() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val state = _uiState.value
            val filter = GmailFilter (
                name = state.filterName,
                senderContains = state.senderContains.takeIf { it.isNotBlank() },
                subjectContains = state.subjectContains.takeIf { it.isNotBlank() }
            )

            val result = createFilter(filter)
            result
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isSaved = true) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Unknown error"
                        )
                    }
                }
        }
    }

}