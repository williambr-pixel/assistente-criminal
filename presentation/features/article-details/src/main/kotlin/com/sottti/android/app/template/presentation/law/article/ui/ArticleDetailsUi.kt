package com.sottti.android.app.template.presentation.law.article.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sottti.android.app.template.domain.law.model.CriminalArticleId
import com.sottti.android.app.template.presentation.design.system.themes.AndroidAppTemplateTheme
import com.sottti.android.app.template.presentation.law.article.data.ArticleDetailsViewModel
import com.sottti.android.app.template.presentation.law.article.model.ArticleDetailsActions
import com.sottti.android.app.template.presentation.law.article.model.ArticleDetailsState
import com.sottti.android.app.template.presentation.previews.PreviewAndroidAppTemplate

@Composable
public fun ArticleDetails(articleId: String) {
    val viewModel = hiltViewModel<ArticleDetailsViewModel, ArticleDetailsViewModel.Factory>(
        creationCallback = { factory -> factory.create(CriminalArticleId(articleId)) },
    )
    ArticleDetails(viewModel)
}

@Composable
private fun ArticleDetails(
    viewModel: ArticleDetailsViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ArticleDetails(
        state = state,
        onAction = viewModel.onAction,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ArticleDetails(
    state: ArticleDetailsState,
    onAction: (ArticleDetailsActions) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    ArticleDetailsContent(
        state = state,
        onAction = onAction,
        scrollBehavior = scrollBehavior,
    )
}

@Composable
@PreviewAndroidAppTemplate
internal fun ArticleDetailsUiPreview(
    @PreviewParameter(ArticleDetailsUiStateProvider::class)
    state: ArticleDetailsState,
) {
    AndroidAppTemplateTheme {
        ArticleDetails(
            state = state,
            onAction = {},
        )
    }
}
