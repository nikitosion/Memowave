package com.memowave.app.ui.screen.main_page.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import com.memowave.app.R
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun LearningMode(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    cornerRadius: Float = 30f,
    figureSize: Dp = 200.dp,
    modeName: String = "Карточки",
    @DrawableRes iconResId: Int
) {
    Box(
        modifier = modifier
            .height(220.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .background(
                MaterialTheme.colorScheme.surfaceContainer,
                RoundedCornerShape(30.dp)
            )
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .drawWithCache {
                    val roundedPolygon = RoundedPolygon(
                        numVertices = 8,
                        radius = figureSize.value,
                        centerX = figureSize.value / 2,
                        centerY = size.height - figureSize.value / 2,
                        rounding = CornerRounding(cornerRadius)
                    )
                    val polygonPath = roundedPolygon.toPath().asComposePath()
                    onDrawBehind {
                        drawPath(polygonPath, color = backgroundColor)
                    }
                },
            contentAlignment = Alignment.BottomStart
        ) {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = "Продолжить",
                tint = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .size(95.dp)
                    .offset(x = figureSize/32, y = -figureSize/32)
            )
        }
        Text(
            modifier = modifier
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp)
                .fillMaxWidth()
                .align(Alignment.TopEnd),
            text = modeName,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.W500,
            textAlign = TextAlign.End
        )
    }
}

@Preview
@Composable
fun LearningModePreview() {
    MemowaveTheme {
        Row(
            modifier = Modifier
                .padding(top = 28.dp)
                .fillMaxWidth()
        ) {
            LearningMode(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 6.dp),
                modeName = "Карточки",
                cornerRadius = 30f,
                figureSize = 250.dp,
                iconResId = R.drawable.playing_cards_24
            )
            LearningMode(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 6.dp),
                modeName = "Перевод",
                cornerRadius = 30f,
                figureSize = 250.dp,
                iconResId = R.drawable.round_translate_24
            )
        }
    }
}