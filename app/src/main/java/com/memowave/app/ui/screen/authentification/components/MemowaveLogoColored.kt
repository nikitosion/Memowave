package com.memowave.app.ui.screen.authentification.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.memowave.app.R

@Composable
fun MemowaveLogoColored(
    modifier: Modifier = Modifier,
    size: Dp = 60.dp
) {
    Image(
        painter = painterResource(id = R.drawable.memowave_logo_colored_no_surface),
        contentDescription = "Memowave Logo",
        modifier = modifier.size(size)
    )
}