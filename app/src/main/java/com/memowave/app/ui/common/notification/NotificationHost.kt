package com.memowave.app.ui.common.notification

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

/**
 * App-wide notification host. Observes [manager], auto-dismisses after [autoDismissMillis],
 * and animates [MemowaveNotification] in and out.
 *
 * Drop into any `Box` where notifications should appear:
 * ```
 * NotificationHost(
 *     manager = appViewModel.notificationManager,
 *     modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding(),
 * )
 * ```
 */
@Composable
fun NotificationHost(
    manager: NotificationManager,
    modifier: Modifier = Modifier,
    autoDismissMillis: Long = 3000L,
) {
    val notification by manager.notificationMessage.collectAsState()
    var visible by remember { mutableStateOf(false) }
    var displayed by remember { mutableStateOf<NotificationMessage?>(null) }

    LaunchedEffect(notification) {
        if (notification != null) {
            displayed = notification
            visible = true
            delay(autoDismissMillis)
            visible = false
        }
    }

    LaunchedEffect(visible) {
        if (!visible && displayed != null) {
            delay(EXIT_ANIM_MILLIS.toLong())
            manager.clearNotification()
            displayed = null
        }
    }

    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(ENTER_ANIM_MILLIS),
        ) + fadeIn(tween(ENTER_ANIM_MILLIS)),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(EXIT_ANIM_MILLIS),
        ) + fadeOut(tween(EXIT_ANIM_MILLIS)),
    ) {
        displayed?.let { current ->
            MemowaveNotification(
                message = current.message,
                type = current.type,
                onDismiss = { visible = false },
            )
        }
    }
}

private const val ENTER_ANIM_MILLIS = 300
private const val EXIT_ANIM_MILLIS = 250
