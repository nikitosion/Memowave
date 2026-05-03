package com.memowave.app.ui.common.media

import androidx.compose.runtime.staticCompositionLocalOf
import com.memowave.app.core.media.ImageUrlResolver

val LocalImageUrlResolver = staticCompositionLocalOf<ImageUrlResolver> {
    error("LocalImageUrlResolver not provided. Wrap your content in CompositionLocalProvider.")
}
