package com.memowave.app.ui.screen.authentification.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.memowave.app.R

@Composable
fun MemowaveLogoColored(
    color: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier,
    size: Dp = 60.dp
) {
    Image(
        painter = painterResource(id = R.drawable.memowave_logo),
        contentDescription = "Memowave Logo",
        colorFilter = ColorFilter.tint(
            color = color,
            blendMode = BlendMode.SrcIn
        ),
        modifier = modifier.size(size),
    )
}