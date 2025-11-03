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
    modifier: Modifier = Modifier
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
        WordStatistics(Color(0xFF00363D))
        WordStatistics(Color(0xFF0F4E57), "345", "Изучение")
        WordStatistics(Color(0xFF006875), "8", "Повторение")
    }
}

@Preview
@Composable
fun BaseWordStaticticsPreview() {
    MemowaveTheme {
        BaseWordStatictics()
    }
}