package com.memowave.app.ui.common.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.memowave.app.ui.common.MemowaveTopBar

/**
 * Унифицированный layout для подэкранов настроек: верхняя панель + scrollable Column.
 *
 * Используется внутри `Surface` из `MainActivity`, который уже учитывает
 * status bar inset через `paddingValues.calculateTopPadding()` — поэтому здесь
 * `statusBarsPadding()` не применяется (иначе получится двойной отступ).
 */
@Composable
fun SettingsScaffold(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(16.dp),
    bottomContent: @Composable ColumnScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        MemowaveTopBar(
            onBackClick = onBackClick,
            title = title,
            bottomContent = bottomContent
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(contentPadding)
                .padding(top = 8.dp, bottom = 24.dp),
            verticalArrangement = verticalArrangement
        ) {
            content()
        }
    }
}

@Composable
fun SettingsContent(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = verticalArrangement,
        content = content
    )
}
