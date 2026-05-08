package com.memowave.app.ui.screen.flashcard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.memowave.app.R
import com.memowave.app.ui.screen.flashcard.FlashcardWordSummary

/**
 * Modal bottom sheet listing every [FlashcardWordSummary] from the just-finished
 * session. Each row is expandable to reveal the full FSRS delta. Only one row is
 * expanded at a time to keep the sheet uncluttered.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordSummaryListSheet(
    summaries: List<FlashcardWordSummary>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var expandedId by remember { mutableStateOf<Long?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item(key = "header") {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    text = stringResource(R.string.flashcard_summary_show_all_sheet_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.W700,
                    textAlign = TextAlign.Center
                )
            }
            items(items = summaries, key = { it.word.id }) { summary ->
                ExpandableWordSummaryRow(
                    summary = summary,
                    expanded = expandedId == summary.word.id,
                    onExpandedChange = { wantOpen ->
                        expandedId = if (wantOpen) summary.word.id else null
                    }
                )
            }
        }
    }
}
