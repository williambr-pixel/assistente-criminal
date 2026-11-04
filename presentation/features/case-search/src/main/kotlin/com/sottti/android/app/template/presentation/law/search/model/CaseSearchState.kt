package com.sottti.android.app.template.presentation.law.search.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.sottti.android.app.template.domain.law.model.MatchConfidence

@Immutable
internal data class CaseSearchState(
    @StringRes val titleResId: Int,
    val query: String = "",
    val hasSearched: Boolean = false,
    val isSearching: Boolean = false,
    val results: List<CaseMatchState> = emptyList(),
)

@Immutable
internal data class CaseMatchState(
    val id: String,
    val code: String,
    val article: String,
    val title: String,
    val confidence: MatchConfidence,
    val matchedTerms: List<String>,
    val hasConflicts: Boolean,
)
