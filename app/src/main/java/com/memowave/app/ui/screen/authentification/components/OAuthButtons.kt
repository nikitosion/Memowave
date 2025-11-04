package com.memowave.app.ui.screen.authentification.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.memowave.app.R

@Composable
fun OAuthButtons(
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        IconButton(
            modifier = Modifier
                .size(65.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = CircleShape
                ),
            onClick = {},
        ) {
            Image(
                modifier = Modifier.size(30.dp),
                painter = painterResource(id = R.drawable.apple_logo_colored),
                contentDescription = "Apple Logo"
            )
        }
        IconButton(
            modifier = Modifier
                .padding(start = 24.dp)
                .size(65.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = CircleShape
                ),
            onClick = {},
        ) {
            Image(
                modifier = Modifier.size(30.dp),
                painter = painterResource(id = R.drawable.yandex_logo_colored),
                contentDescription = "Yandex Logo"
            )
        }
        IconButton(
            modifier = Modifier
                .padding(start = 24.dp)
                .size(65.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = CircleShape
                ),
            onClick = {},
        ) {
            Image(
                modifier = Modifier.size(30.dp),
                painter = painterResource(id = R.drawable.google_logo_colored),
                contentDescription = "Google Logo"
            )
        }
    }
}