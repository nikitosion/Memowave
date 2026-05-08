package com.memowave.app.ui.screen.translation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.memowave.app.R
import com.memowave.app.domain.model.Rating
import com.memowave.app.domain.model.Word
import com.memowave.app.ui.screen.translation.TranslationCheckResult

@Composable
fun TranslationGameContent(
    word: Word,
    userInput: String,
    isAwaitingFeedback: Boolean,
    checkResult: TranslationCheckResult?,
    hintLettersShown: Int,
    answerRevealed: Boolean,
    onInputChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onShowHint: () -> Unit,
    onRevealAnswer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(word.id) {
        if (!isAwaitingFeedback) {
            focusRequester.requestFocus()
            keyboard?.show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp, bottom = 16.dp)
    ) {
        // Word card with the original
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.translation_prompt_label),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = word.original,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.W700,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!word.note.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = word.note ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
                if (checkResult != null) {
                    Spacer(Modifier.height(20.dp))
                    TranslationFeedbackCard(result = checkResult)
                }
            }
        }

        // Input + actions
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                value = userInput,
                onValueChange = onInputChange,
                singleLine = true,
                enabled = !isAwaitingFeedback,
                placeholder = {
                    Text(
                        text = stringResource(R.string.translation_input_hint),
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                shape = RoundedCornerShape(20.dp),
                textStyle = MaterialTheme.typography.titleMedium,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onSubmit() }),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    disabledContainerColor = feedbackContainerColor(checkResult),
                    disabledTextColor = MaterialTheme.colorScheme.onSurface
                )
            )

            // Hint row (shown only before submission)
            if (!isAwaitingFeedback) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        onClick = onShowHint,
                        enabled = !answerRevealed,
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(
                            modifier = Modifier.size(18.dp),
                            painter = painterResource(R.drawable.round_help_outline_24),
                            contentDescription = null
                        )
                        Spacer(Modifier.size(6.dp))
                        Text(
                            text = stringResource(
                                R.string.translation_show_first_letter,
                                hintLettersShown
                            ),
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                    OutlinedButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        onClick = onRevealAnswer,
                        enabled = !answerRevealed,
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(
                            modifier = Modifier.size(18.dp),
                            painter = painterResource(R.drawable.round_visibility_24),
                            contentDescription = null
                        )
                        Spacer(Modifier.size(6.dp))
                        Text(
                            text = stringResource(R.string.translation_reveal),
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                onClick = onSubmit,
                enabled = !isAwaitingFeedback && userInput.isNotBlank(),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = stringResource(R.string.translation_done),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.W700
                )
            }
        }
    }
}

@Composable
private fun feedbackContainerColor(result: TranslationCheckResult?): Color {
    if (result == null) return MaterialTheme.colorScheme.surfaceContainerLow
    return when (result.rating) {
        Rating.Easy, Rating.Good -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        Rating.Hard -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.18f)
        Rating.Again -> MaterialTheme.colorScheme.error.copy(alpha = 0.14f)
    }
}
