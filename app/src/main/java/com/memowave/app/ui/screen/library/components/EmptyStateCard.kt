package com.memowave.app.ui.screen.library.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun EmptyStateCard(
    title: String,
    subtitle: String,
    primaryActionText: String,
    onPrimaryActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.W600
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(top = 4.dp)
            )
            Button(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                onClick = onPrimaryActionClick
            ) {
                Text(primaryActionText)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyStateCardPreview() {
    MemowaveTheme {
        EmptyStateCard(
            title = "Нет данных",
            subtitle = "Добавьте элементы, чтобы увидеть их здесь.",
            primaryActionText = "Добавить",
            onPrimaryActionClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
