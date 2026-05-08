package com.memowave.app.ui.screen.main_page.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import com.memowave.app.R
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun InterestingFacts(
    modifier: Modifier = Modifier,
    title: String = "Волны заимствований",
    body: String = "Английский язык заимствовал слова более чем из 350 разных языков, что делает его словарь одним из самых «мировых» и гибридных среди всех современных языков.",
    onPrev: () -> Unit = {},
    onNext: () -> Unit = {},
) {
    val figureColor = MaterialTheme.colorScheme.onPrimaryContainer

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(30.dp))
            .background(
                MaterialTheme.colorScheme.surfaceContainer,
                RoundedCornerShape(30.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(30.dp)
                )
                .padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(color = Color(0x1A004F58), shape = RoundedCornerShape(20.dp))
            ) {
                Icon(
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.Center),
                    painter = painterResource(id = R.drawable.round_public_24),
                    contentDescription = "Earth icon",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Column() {
                Text(
                    modifier = Modifier
                        .height(50.dp)
                        .wrapContentHeight(align = Alignment.CenterVertically)
                        .padding(start = 12.dp),
                    text = "А вы знали?",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.W500
                )
                Column(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .background(color = Color(0x14004F58), shape = RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = body,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onPrev,
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .height(40.dp)
                            .width(40.dp),
                        colors = IconButtonColors(
                            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContainerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                alpha = 0.5f
                            ),
                            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_chevron_left_24),
                            contentDescription = "Previous fact",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    IconButton(
                        onClick = onNext,
                        modifier = Modifier
                            .padding(top = 12.dp, start = 16.dp)
                            .height(40.dp)
                            .width(52.dp),
                        colors = IconButtonColors(
                            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContainerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                alpha = 0.5f
                            ),
                            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_chevron_right_24),
                            contentDescription = "Next fact",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .drawWithCache {
                    val roundedPolygon = RoundedPolygon(
                        numVertices = 8,
                        radius = 150f,
                        centerX = 150f / 2,
                        centerY = size.height - 150f / 2,
                        rounding = CornerRounding(60f)
                    )
                    val polygonPath = roundedPolygon.toPath().asComposePath()
                    onDrawBehind {
                        drawPath(polygonPath, color = figureColor)
                    }
                },
            contentAlignment = Alignment.BottomStart
        ) {}
    }
}

@Preview
@Composable
fun InterestingFactsPreview() {
    MemowaveTheme {
        InterestingFacts()
    }
}