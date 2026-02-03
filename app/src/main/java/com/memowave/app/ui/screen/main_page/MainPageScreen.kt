package com.memowave.app.ui.screen.main_page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.memowave.app.AppViewModel
import com.memowave.app.R
import com.memowave.app.ui.screen.main_page.components.BaseWordStatictics
import com.memowave.app.ui.screen.main_page.components.ContinueLearningButton
import com.memowave.app.ui.screen.main_page.components.InterestingFacts
import com.memowave.app.ui.screen.main_page.components.LearningMode
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun MainPageRoute(
    appViewModel: AppViewModel = hiltViewModel()
) {
    MainPageScreen(appViewModel)
}

data class LearningModeConfig(
    val id: String,
    val iconResId: Int,
)

private val LEARNING_MODES = listOf(
    LearningModeConfig("Каротчки", R.drawable.playing_cards_24),
    LearningModeConfig("Перевод", R.drawable.round_translate_24),
    LearningModeConfig("Викторина", R.drawable.electric_bolt_24),
    LearningModeConfig("Слово-пазл", R.drawable.baseline_extension_24),
)

@Composable
fun MainPageScreen(
    appViewModel: AppViewModel? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .padding(top = 32.dp, bottom = 32.dp)
    ) {
        ContinueLearningButton()
        BaseWordStatictics(modifier = Modifier.padding(top = 28.dp))

        LEARNING_MODES.chunked(2).forEach { rowModes ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = if (rowModes == LEARNING_MODES.chunked(2).first()) 28.dp
                        else 12.dp
                    )
            ) {
                rowModes.forEachIndexed { index, mode ->
                    LearningMode(
                        modifier = Modifier
                            .weight(1f)
                            .padding(
                                start = if (index == 0) 0.dp
                                else 6.dp,
                                end = if (index == 1) 0.dp
                                else 6.dp
                            ),
                        cornerRadius = 30f,
                        figureSize = 250.dp,
                        modeName = mode.id,
                        iconResId = mode.iconResId
                    )
                }
            }
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .height(70.dp),
            shape = RoundedCornerShape(30.dp),
            colors = buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            onClick = { /* TODO: Implement add new mode action */ }) {
            Text(
                "Все режимы...",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.W500
            )
        }
        InterestingFacts(modifier = Modifier.padding(top = 28.dp))
    }
}

@Preview(device = "spec:height=1500dp,width=411dp")
@Composable
fun MainPageScreenPreview() {
    MemowaveTheme {
        MainPageScreen()
    }
}