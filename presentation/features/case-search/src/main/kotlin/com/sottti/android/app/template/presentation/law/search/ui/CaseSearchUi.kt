package com.sottti.android.app.template.presentation.law.search.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sottti.android.app.template.presentation.design.system.themes.AndroidAppTemplateTheme
import com.sottti.android.app.template.presentation.law.search.data.CaseSearchViewModel
import com.sottti.android.app.template.presentation.law.search.model.CaseSearchActions
import com.sottti.android.app.template.presentation.law.search.model.CaseSearchState
import com.sottti.android.app.template.presentation.previews.PreviewAndroidAppTemplate

@Composable
public fun CaseSearch() {
    CaseSearch(hiltViewModel<CaseSearchViewModel>())
}

@Composable
private fun CaseSearch(
    viewModel: CaseSearchViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CaseSearch(
        state = state,
        onAction = viewModel.onAction,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CaseSearch(
    state: CaseSearchState,
    onAction: (CaseSearchActions) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    CaseSearchContent(
        state = state,
        onAction = onAction,
        scrollBehavior = scrollBehavior,
    )
}

@Composable
@PreviewAndroidAppTemplate
internal fun CaseSearchUiPreview(
    @PreviewParameter(CaseSearchUiStateProvider::class)
    state: CaseSearchState,
) {
    AndroidAppTemplateTheme {
        CaseSearch(
            state = state,
            onAction = {},
        )
    }
}
