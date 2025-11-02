package com.memowave.app.ui.screen.main_page.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun WordStatistics(
    boxColor: Color = Color(0xFF00363D),
    countText: String = "123",
    labelText: String = "Новые",
    modifier: Modifier = Modifier,
) {
    Column () {
        Text(
            modifier = modifier.width(80.dp).background(color = boxColor, shape = RoundedCornerShape(30.dp)).padding(vertical = 20.dp),
            text = countText,
            color = MaterialTheme.colorScheme.primaryContainer,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = Center
        )
        Text(
            modifier = modifier.width(80.dp).padding(top = 8.dp),
            text = labelText,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleSmall,
            textAlign = Center,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
fun WordStatisticsPreview() {
    MemowaveTheme {
        WordStatistics(Color(0xFF00363D))
    }
}