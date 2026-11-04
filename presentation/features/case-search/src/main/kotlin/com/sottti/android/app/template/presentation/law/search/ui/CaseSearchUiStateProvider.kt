package com.sottti.android.app.template.presentation.law.search.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sottti.android.app.template.presentation.law.search.R
import com.sottti.android.app.template.presentation.law.search.fixtures.fixtureCaseSearchResults
import com.sottti.android.app.template.presentation.law.search.model.CaseSearchState

internal class CaseSearchUiStateProvider : PreviewParameterProvider<CaseSearchState> {
    override val values: Sequence<CaseSearchState>
        get() = sequence {
            yield(initialState)
            yield(searchingState)
            yield(loadedState)
            yield(emptyResultsState)
        }

    private val initialState = CaseSearchState(titleResId = R.string.case_search_title)

    private val searchingState = CaseSearchState(
        titleResId = R.string.case_search_title,
        query = "O autor, armado, rendeu a vítima e levou o celular.",
        hasSearched = true,
        isSearching = true,
    )

    private val loadedState = CaseSearchState(
        titleResId = R.string.case_search_title,
        query = "O autor, armado, rendeu a vítima e levou o celular.",
        hasSearched = true,
        results = fixtureCaseSearchResults,
    )

    private val emptyResultsState = CaseSearchState(
        titleResId = R.string.case_search_title,
        query = "Discussão sobre cláusula de contrato de aluguel.",
        hasSearched = true,
    )
}
