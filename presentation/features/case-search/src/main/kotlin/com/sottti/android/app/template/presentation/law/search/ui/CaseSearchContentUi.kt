package com.sottti.android.app.template.presentation.law.search.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.sottti.android.app.template.domain.law.model.MatchConfidence
import com.sottti.android.app.template.presentation.design.system.colors.color.composition.local.colors
import com.sottti.android.app.template.presentation.design.system.dimensions.composition.local.dimensions
import com.sottti.android.app.template.presentation.design.system.progress.indicators.ProgressIndicator
import com.sottti.android.app.template.presentation.design.system.shapes.composition.local.shapes
import com.sottti.android.app.template.presentation.design.system.text.Text
import com.sottti.android.app.template.presentation.design.system.top.bars.ui.MainTopBar
import com.sottti.android.app.template.presentation.law.search.R
import com.sottti.android.app.template.presentation.law.search.model.CaseMatchState
import com.sottti.android.app.template.presentation.law.search.model.CaseSearchActions
import com.sottti.android.app.template.presentation.law.search.model.CaseSearchActions.ClearQuery
import com.sottti.android.app.template.presentation.law.search.model.CaseSearchActions.Search
import com.sottti.android.app.template.presentation.law.search.model.CaseSearchActions.ShowArticleDetail
import com.sottti.android.app.template.presentation.law.search.model.CaseSearchActions.UpdateQuery
import com.sottti.android.app.template.presentation.law.search.model.CaseSearchActions.UseExample
import com.sottti.android.app.template.presentation.law.search.model.CaseSearchState

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun CaseSearchContent(
    state: CaseSearchState,
    onAction: (CaseSearchActions) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { MainTopBar(titleResId = state.titleResId, scrollBehavior = scrollBehavior) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(dimensions.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(dimensions.spacing.medium),
        ) {
            item(key = "intro") { Intro() }
            item(key = "query") { QueryField(state = state, onAction = onAction) }
            item(key = "actions") { QueryActions(state = state, onAction = onAction) }

            if (state.isSearching) {
                item(key = "loading") { Loading() }
            }

            if (!state.hasSearched) {
                item(key = "examples") { Examples(onAction = onAction) }
            } else if (!state.isSearching) {
                resultsSection(results = state.results, onAction = onAction)
            }

            item(key = "disclaimer") { Disclaimer() }
        }
    }
}

@Composable
private fun Intro() {
    Column(verticalArrangement = Arrangement.spacedBy(dimensions.spacing.small)) {
        Text.Headline.Small(
            text = stringResource(R.string.case_search_intro_title),
            textColor = colors.onBackground,
        )
        Text.Body.Medium(
            text = stringResource(R.string.case_search_intro_body),
            textColor = colors.onSurfaceVariant,
        )
    }
}

@Composable
private fun QueryField(
    state: CaseSearchState,
    onAction: (CaseSearchActions) -> Unit,
) {
    OutlinedTextField(
        value = state.query,
        onValueChange = { query -> onAction(UpdateQuery(query)) },
        modifier = Modifier
            .fillMaxWidth()
            .testTag(CASE_SEARCH_QUERY_TEST_TAG),
        label = { Text.Vanilla(R.string.case_search_query_label) },
        placeholder = { Text.Vanilla(R.string.case_search_query_placeholder) },
        supportingText = { Text.Vanilla(R.string.case_search_query_support) },
        minLines = MINIMUM_QUERY_LINES,
    )
}

@Composable
private fun QueryActions(
    state: CaseSearchState,
    onAction: (CaseSearchActions) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensions.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(
            onClick = { onAction(Search) },
            modifier = Modifier
                .weight(1f)
                .testTag(CASE_SEARCH_SUBMIT_TEST_TAG),
            enabled = state.query.isNotBlank() && !state.isSearching,
        ) {
            Text.Vanilla(R.string.case_search_submit)
        }
        TextButton(
            onClick = { onAction(ClearQuery) },
            modifier = Modifier.testTag(CASE_SEARCH_CLEAR_TEST_TAG),
            enabled = state.query.isNotBlank() || state.hasSearched,
        ) {
            Text.Vanilla(R.string.case_search_clear)
        }
    }
}

@Composable
private fun Loading() {
    ProgressIndicator(modifier = Modifier.fillMaxWidth())
}

@Composable
private fun Examples(onAction: (CaseSearchActions) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(dimensions.spacing.small)) {
        Text.Label.Large(
            text = stringResource(R.string.case_search_examples_title),
            textColor = colors.onBackground,
        )
        exampleResIds.forEach { exampleResId ->
            ExampleCard(exampleResId = exampleResId, onAction = onAction)
        }
    }
}

@Composable
private fun ExampleCard(
    @StringRes exampleResId: Int,
    onAction: (CaseSearchActions) -> Unit,
) {
    val description = stringResource(exampleResId)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAction(UseExample(description)) }
    ) {
        Column(
            modifier = Modifier.padding(dimensions.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(dimensions.spacing.extraSmall),
        ) {
            Text.Body.Medium(text = description, textColor = colors.onSurface)
            Text.Label.Small(
                text = stringResource(R.string.case_search_example_action),
                textColor = colors.primary,
            )
        }
    }
}

private fun LazyListScope.resultsSection(
    results: List<CaseMatchState>,
    onAction: (CaseSearchActions) -> Unit,
) {
    if (results.isEmpty()) {
        item(key = "no results") { NoResults() }
        return
    }

    item(key = "results header") { ResultsHeader(count = results.size) }
    items(items = results, key = CaseMatchState::id) { result ->
        ResultCard(result = result, onAction = onAction)
    }
}

@Composable
private fun ResultsHeader(count: Int) {
    Text.Title.Small(
        text = pluralStringResource(R.plurals.case_search_results_header, count, count),
        textColor = colors.onBackground,
    )
}

@Composable
private fun ResultCard(
    result: CaseMatchState,
    onAction: (CaseSearchActions) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(CASE_SEARCH_RESULT_TEST_TAG)
            .clickable { onAction(ShowArticleDetail(articleId = result.id)) }
    ) {
        Column(
            modifier = Modifier.padding(dimensions.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(dimensions.spacing.small),
        ) {
            ResultHeader(result = result)
            Text.Body.Medium(text = result.title, textColor = colors.onSurface)
            Text.Body.Small(text = result.code, textColor = colors.onSurfaceVariant)

            if (result.matchedTerms.isNotEmpty()) {
                Text.Body.Small(
                    text = stringResource(
                        R.string.case_search_matched_terms,
                        result.matchedTerms.take(MAX_TERMS_SHOWN).joinToString(separator = ", "),
                    ),
                    textColor = colors.onSurfaceVariant,
                )
            }

            if (result.hasConflicts) {
                Text.Body.Small(
                    text = stringResource(R.string.case_search_conflict_warning),
                    textColor = colors.error,
                )
            }
        }
    }
}

@Composable
private fun ResultHeader(result: CaseMatchState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensions.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text.Title.Medium(text = result.article, textColor = colors.onBackground)
        ConfidenceBadge(confidence = result.confidence)
    }
}

@Composable
private fun ConfidenceBadge(confidence: MatchConfidence) {
    Surface(
        color = confidence.containerColor(),
        shape = shapes.roundedCorner.small,
    ) {
        Text.Label.Small(
            text = stringResource(confidence.labelRes()),
            textColor = confidence.labelColor(),
            modifier = Modifier.padding(
                horizontal = dimensions.spacing.smallMedium,
                vertical = dimensions.spacing.extraSmall,
            ),
        )
    }
}

@Composable
private fun MatchConfidence.containerColor() = when (this) {
    MatchConfidence.HIGH -> colors.primaryContainer
    MatchConfidence.MEDIUM -> colors.secondaryContainer
    MatchConfidence.LOW -> colors.surfaceVariant
}

@Composable
private fun MatchConfidence.labelColor() = when (this) {
    MatchConfidence.HIGH -> colors.onPrimaryContainer
    MatchConfidence.MEDIUM -> colors.onSecondaryContainer
    MatchConfidence.LOW -> colors.onSurfaceVariant
}

@StringRes
private fun MatchConfidence.labelRes(): Int = when (this) {
    MatchConfidence.HIGH -> R.string.case_search_confidence_high
    MatchConfidence.MEDIUM -> R.string.case_search_confidence_medium
    MatchConfidence.LOW -> R.string.case_search_confidence_low
}

@Composable
private fun NoResults() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(CASE_SEARCH_NO_RESULTS_TEST_TAG)
    ) {
        Column(
            modifier = Modifier.padding(dimensions.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(dimensions.spacing.small),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text.Title.Small(
                text = stringResource(R.string.case_search_no_results_title),
                textColor = colors.onSurface,
                textAlign = TextAlign.Center,
            )
            Text.Body.Medium(
                text = stringResource(R.string.case_search_no_results_body),
                textColor = colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun Disclaimer() {
    Text.Body.Small(
        text = stringResource(R.string.case_search_disclaimer),
        textColor = colors.onSurfaceVariant,
    )
}

private const val MINIMUM_QUERY_LINES = 4
private const val MAX_TERMS_SHOWN = 6

private val exampleResIds = listOf(
    R.string.case_search_example_armed_robbery,
    R.string.case_search_example_domestic_violence,
    R.string.case_search_example_fraud,
    R.string.case_search_example_drugs,
)
