package com.notisiren.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notisiren.shared.domain.usecase.GetAllFilters
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import android.content.Context
import com.notisiren.feature.notifications.NotificationListenerStatusChecker
import dagger.hilt.android.qualifiers.ApplicationContext

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    getAllFilters: GetAllFilters,
    private val statusChecker: NotificationListenerStatusChecker
): ViewModel(){
    val uiState: StateFlow<MainUiState> =
        getAllFilters()
            .map {filters -> MainUiState(
                filters= filters,
                isLoading = false,
                isNotificationListenerEnabled = statusChecker.isNotificationListenerEnabled(context))}
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = MainUiState()
            )
}