package com.memowave.app.ui.screen.flashcard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memowave.app.R
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun FlashcardNumberedAnswerButtons(
    options: List<String>,
    correctIndex: Int,
    selectedIndex: Int?,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (options.size != 4) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NumberedAnswerButton(
                text = options[0],
                state = buttonState(index = 0, selectedIndex = selectedIndex, correctIndex = correctIndex),
                onClick = { onSelect(0) },
                modifier = Modifier.weight(1f)
            )
            NumberedAnswerButton(
                text = options[1],
                state = buttonState(index = 1, selectedIndex = selectedIndex, correctIndex = correctIndex),
                onClick = { onSelect(1) },
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NumberedAnswerButton(
                text = options[2],
                state = buttonState(index = 2, selectedIndex = selectedIndex, correctIndex = correctIndex),
                onClick = { onSelect(2) },
                modifier = Modifier.weight(1f)
            )
            NumberedAnswerButton(
                text = options[3],
                state = buttonState(index = 3, selectedIndex = selectedIndex, correctIndex = correctIndex),
                onClick = { onSelect(3) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private enum class AnswerButtonState {
    Unanswered,            // no pick yet — outlined, clickable
    SelectedCorrect,       // user picked and was right — filled green
    SelectedWrong,         // user picked and was wrong — filled red
    RevealedCorrect,       // user picked something else, this is the correct — outlined green
    RevealedWrong          // not picked, not correct — dim outlined
}

private fun buttonState(
    index: Int,
    selectedIndex: Int?,
    correctIndex: Int
): AnswerButtonState {
    if (selectedIndex == null) return AnswerButtonState.Unanswered
    val isThisPicked = index == selectedIndex
    val isThisCorrect = index == correctIndex
    return when {
        isThisPicked && isThisCorrect -> AnswerButtonState.SelectedCorrect
        isThisPicked && !isThisCorrect -> AnswerButtonState.SelectedWrong
        !isThisPicked && isThisCorrect -> AnswerButtonState.RevealedCorrect
        else -> AnswerButtonState.RevealedWrong
    }
}

@Composable
private fun NumberedAnswerButton(
    text: String,
    state: AnswerButtonState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme

    when (state) {
        AnswerButtonState.Unanswered -> {
            OutlinedButton(
                modifier = modifier.height(64.dp),
                onClick = onClick,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = scheme.onSurface
                )
            ) {
                AnswerLabel(text = text)
            }
        }

        AnswerButtonState.SelectedCorrect -> FilledAnswer(
            text = text,
            iconRes = R.drawable.round_check_24,
            container = scheme.primary,
            content = scheme.onPrimary,
            modifier = modifier
        )

        AnswerButtonState.SelectedWrong -> FilledAnswer(
            text = text,
            iconRes = R.drawable.round_close_24,
            container = scheme.error,
            content = scheme.onError,
            modifier = modifier
        )

        AnswerButtonState.RevealedCorrect -> OutlinedAnswer(
            text = text,
            iconRes = R.drawable.round_check_24,
            border = scheme.primary.copy(alpha = 0.2f),
            content = scheme.primary.copy(alpha = 0.5f),
            modifier = modifier
        )

        AnswerButtonState.RevealedWrong -> OutlinedAnswer(
            text = text,
            iconRes = R.drawable.round_close_24,
            border = scheme.error.copy(alpha = 0.18f),
            content = scheme.error.copy(alpha = 0.45f),
            modifier = modifier
        )
    }
}

@Composable
private fun FilledAnswer(
    text: String,
    iconRes: Int,
    container: Color,
    content: Color,
    modifier: Modifier
) {
    Button(
        modifier = modifier.height(64.dp),
        onClick = {},
        enabled = false,
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            disabledContainerColor = container,
            disabledContentColor = content
        )
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        AnswerLabel(text = text, modifier = Modifier.padding(start = 6.dp))
    }
}

@Composable
private fun OutlinedAnswer(
    text: String,
    iconRes: Int,
    border: Color,
    content: Color,
    modifier: Modifier
) {
    OutlinedButton(
        modifier = modifier.height(64.dp),
        onClick = {},
        enabled = false,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, border),
        colors = ButtonDefaults.outlinedButtonColors(
            disabledContentColor = content
        )
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        AnswerLabel(text = text, modifier = Modifier.padding(start = 6.dp))
    }
}

@Composable
private fun AnswerLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.W600,
        textAlign = TextAlign.Center,
        maxLines = 2
    )
}

@Preview(showBackground = true, widthDp = 400)
@Composable
private fun NumberedAnswerButtonsUnansweredPreview() {
    MemowaveTheme {
        FlashcardNumberedAnswerButtons(
            options = listOf("Привет", "Пока", "Собака", "Кошка"),
            correctIndex = 2,
            selectedIndex = null,
            onSelect = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
private fun NumberedAnswerButtonsCorrectPickPreview() {
    MemowaveTheme {
        FlashcardNumberedAnswerButtons(
            options = listOf("Привет", "Пока", "Собака", "Кошка"),
            correctIndex = 2,
            selectedIndex = 2,
            onSelect = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
private fun NumberedAnswerButtonsWrongPickPreview() {
    MemowaveTheme {
        FlashcardNumberedAnswerButtons(
            options = listOf("Привет", "Пока", "Собака", "Кошка"),
            correctIndex = 2,
            selectedIndex = 0,
            onSelect = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
