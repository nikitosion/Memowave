package com.memowave.app.ui.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.memowave.app.R
import com.memowave.app.ui.theme.MemowaveTheme
import com.memowave.app.ui.theme.bodyFontFamily

/**
 * Text composable that renders digits with a separate font ([digitFontFamily], defaults to Inter)
 * and all other characters with the font from [style].
 *
 * Every character gets an explicit [SpanStyle] to guarantee the correct font is applied,
 * regardless of how [Text] merges styles internally.
 *
 * Usage: anywhere you want mixed-font text with distinct digit styling,
 * e.g. "3/15 . Изучение" → digits in Inter, letters in Bagel Fat One / EB Garamond.
 *
 * @param text The text to display.
 * @param style Base TextStyle (determines the default font for non-digit characters).
 * @param accentFontFamily Font family used for digits and accent characters. Defaults to [bodyFontFamily] (Inter).
 * @param accentFontWeight Optional override for accent font weight.
 * @param accentChars Additional characters (besides digits) that should use [accentFontFamily].
 */

private val defaultAccentChars = setOf('/', '#', '%', '+', '-')

@Composable
fun MixedFontText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleLarge,
    fontWeight: FontWeight? = null,
    color: Color = Color.Unspecified,
    accentFontFamily: FontFamily = bodyFontFamily,
    accentFontWeight: FontWeight? = null,
    accentChars: Set<Char> = defaultAccentChars
) {
    val resolvedWeight = fontWeight ?: style.fontWeight

    val accentSpan = SpanStyle(
        fontFamily = accentFontFamily,
        fontWeight = accentFontWeight ?: resolvedWeight
    )
    val textSpan = SpanStyle(
        fontFamily = style.fontFamily,
        fontWeight = resolvedWeight
    )

    val annotated = buildAnnotatedString {
        text.forEach { char ->
            withStyle(if (char.isDigit() || char in accentChars) accentSpan else textSpan) {
                append(char)
            }
        }
    }

    Text(
        text = annotated,
        modifier = modifier,
        style = style,
        color = color
    )
}

@Preview(showBackground = true)
@Composable
private fun MixedFontTextPreview() {
    MemowaveTheme {
        MixedFontText(
            text = "3/15 \u2022 Изучение",
            accentFontFamily = FontFamily(
                Font(R.font.bagelfatone_regular)
            )
        )
    }
}
