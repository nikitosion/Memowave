package com.memowave.app.ui.common.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memowave.app.R

@Composable
fun CustomSnackbar(
    message: String, type: NotificationType, modifier: Modifier = Modifier
) {
    val iconColor = when (type) {
        is NotificationType.Success -> Color(0xFF4CAF50)
        is NotificationType.Error -> MaterialTheme.colorScheme.error
    }

    val iconRes = when (type) {
        is NotificationType.Success -> R.drawable.round_check_24
        is NotificationType.Error -> R.drawable.round_close_24
    }

    Box(
        modifier = modifier
            .padding(16.dp)
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(30.dp))
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(30.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(
                        color = iconColor, shape = CircleShape
                    ), contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surfaceContainer,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(
    showBackground = true, name = "Success Snackbar"
)
@Composable
fun CustomSnackbarSuccessPreview() {
    MaterialTheme {
        Surface {
            CustomSnackbar(
                message = "Successfully signed in!", type = NotificationType.Success
            )
        }
    }
}

@Preview(showBackground = true, name = "Error Snackbar")
@Composable
fun CustomSnackbarErrorPreview() {
    MaterialTheme {
        Surface {
            CustomSnackbar(
                message = "Session expired. Please sign in again", type = NotificationType.Error
            )
        }
    }
}
