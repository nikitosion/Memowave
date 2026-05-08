package com.memowave.app.ui.screen.main_page.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun BaseWordStatictics(
    modifier: Modifier = Modifier,
    newCount: Int = 0,
    dueCount: Int = 0,
    learnedCount: Int = 0,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(30.dp)
            )
            .padding(24.dp)
    ) {
        WordStatistics(Color(0xFF00363D), newCount.toString(), "Новые")
        WordStatistics(Color(0xFF0F4E57), dueCount.toString(), "На повторении")
        WordStatistics(Color(0xFF006875), learnedCount.toString(), "Изучено")
    }
}

@Preview
@Composable
fun BaseWordStaticticsPreview() {
    MemowaveTheme {
        BaseWordStatictics(newCount = 12, dueCount = 5, learnedCount = 87)
    }
}