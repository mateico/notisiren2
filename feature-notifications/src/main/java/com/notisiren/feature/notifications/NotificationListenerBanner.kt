package com.notisiren.feature.notifications

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notisiren.uicomponents.theme.NotiSirenTheme
import kotlinx.coroutines.FlowPreview

@Composable
fun NotificationListenerBanner(
    onDismiss: () -> Unit,
    onEnable: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = stringResource(R.string.notification_listener_disabled_title),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TextButton(
            onClick = onEnable,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(stringResource(R.string.notification_listener_enable_button))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationListenerBannerPreview() {
    NotiSirenTheme {
        NotificationListenerBanner(
            onDismiss = {},
            onEnable = {}
        )
    }
}
