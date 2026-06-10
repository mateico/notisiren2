package com.notisiren.feature.filters

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.notisiren.feature.filters.R

@Composable
fun FilterCreationScreen(
    onNavigateBack: () -> Unit,
    viewModel: FilterCreationViewModel = hiltViewModel()
) {

    val state by viewModel.uiState.collectAsState()
    FilterCreationContent(
        state = state,
        onNavigateBack = onNavigateBack,
        onFilterNameChanged = viewModel::onFilterNameChanged,
        onSenderContainsChanged = viewModel::onSenderContainsChanged,
        onSubjectContainsChanged = viewModel::onSubjectContainsChanged,
        onSaveFilter = viewModel::onSaveFilter,
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterCreationContent(
    state: FilterCreationUiState,
    onNavigateBack: () -> Unit,
    onFilterNameChanged: (String) -> Unit,
    onSenderContainsChanged: (String) -> Unit,
    onSubjectContainsChanged: (String) -> Unit,
    onSaveFilter: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.filter_creation_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.filter_creation_back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = state.filterName,
                onValueChange = onFilterNameChanged,
                label = { Text(stringResource(R.string.filter_creation_name_label)) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.senderContains,
                onValueChange = onSenderContainsChanged,
                label = { Text(stringResource(R.string.filter_creation_sender_label)) },
                placeholder = { Text(stringResource(R.string.filter_creation_sender_hint)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.subjectContains,
                onValueChange = onSubjectContainsChanged,
                label = { Text(stringResource(R.string.filter_creation_subject_label)) },
                placeholder = { Text(stringResource(R.string.filter_creation_subject_hint)) },
                modifier = Modifier.fillMaxWidth()
            )

            if (state.errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onSaveFilter,
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(stringResource(R.string.filter_creation_save))
            }
        }

    }


}