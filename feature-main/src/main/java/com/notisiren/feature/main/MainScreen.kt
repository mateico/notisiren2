package com.notisiren.feature.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.notisiren.shared.domain.model.GmailFilter
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notisiren.feature.notifications.NotificationListenerBanner
import com.notisiren.uicomponents.theme.NotiSirenTheme

@Composable
fun MainScreen(
    onAddFilter: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    MainContent(state = state, onAddFilter = onAddFilter)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    state: MainUiState,
    onAddFilter: () -> Unit
) {

    val context = LocalContext.current





    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.main_title))})
        },
        floatingActionButton = {
                FloatingActionButton(onClick = onAddFilter) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.main_add_filter)
                    )
                }
        }
    ) { innerPadding ->

        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {



            if (!state.isNotificationListenerEnabled) {
                NotificationListenerBanner(
                    onDismiss = { /* optional: add state to dismiss */ },
                    onEnable = {
                        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                        context.startActivity(intent)
                    }
                )
            } else {

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.filters.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.main_empty))
                }
            }
            else -> {
                LazyColumn(modifier = Modifier) {
                    items(state.filters, key = { it.id }) { filter ->
                        FilterRow(filter)
                    }
                }
            }}
        }
    }

    }
}

@Composable
private fun FilterRow(filter: GmailFilter) {
    ListItem(
        headlineContent = { Text(filter.name) },
        supportingContent = {
            val criteria = listOfNotNull(filter.senderContains, filter.subjectContains)
            Text(criteria.joinToString(" . "))
        }
    )
}

val uiState = MainUiState(
    filters = emptyList(),
    isLoading = false,
    isNotificationListenerEnabled = false
)

@Preview(showBackground = true)
@Composable
fun MainContentPreview() {
    NotiSirenTheme {
        MainContent(uiState) { }
    }
}