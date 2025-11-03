package com.memowave.app.ui.screen.main_page.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memowave.app.R
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun ContinueLearningButton(
    modifier: Modifier = Modifier
) {
    val textColor = Color(0xFF9EEFFE)

    Button(
        onClick = { /* TODO: Обработчик нажатия */ },
        modifier = modifier
            .height(100.dp)
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF205A63),
                        Color(0xFF00363D)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                ),
                shape = RoundedCornerShape(30.dp)
            ),
        shape = RoundedCornerShape(30.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White,
            disabledContainerColor = Color.Gray.copy(alpha = 0.5f),
            disabledContentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column() {
                Text(
                    text = "Готов продолжить?",
                    color = textColor,
                    style = MaterialTheme.typography.headlineMedium,
                )
                Row() {
                    Text(
                        text = "Режим:",
                        color = textColor,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.playing_cards_24),
                        contentDescription = "Продолжить",
                        tint = textColor,
                        modifier = Modifier.padding(horizontal = 4.dp).size(20.dp).rotate(180f)
                    )
                    Text(
                        text = " Изучение новых слов",
                        color = textColor,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            Icon(
                painter = painterResource(id = R.drawable.outline_arrow_circle_right_24),
                contentDescription = "Продолжить",
                tint = textColor,
                modifier = Modifier.size(35.dp)
            )
        }
    }
}

@Preview
@Composable
fun ContinueLearningButtonPreview() {
    MemowaveTheme {
        ContinueLearningButton()
    }
}