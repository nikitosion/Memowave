package com.memowave.app.ui.screen.main_page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memowave.app.ui.screen.main_page.components.BaseWordStatictics
import com.memowave.app.ui.screen.main_page.components.ContinueLearningButton
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun MainPageRoute() {
    MainPageScreen()
}

@Composable
fun MainPageScreen() {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 60.dp)) {
        ContinueLearningButton()
        BaseWordStatictics(modifier = Modifier.padding(top = 28.dp))
    }
}

@Preview
@Composable
fun MainPageScreenPreview() {
    MemowaveTheme {
        MainPageScreen()
    }
}