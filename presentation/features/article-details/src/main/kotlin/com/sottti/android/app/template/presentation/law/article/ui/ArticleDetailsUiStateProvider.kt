package com.sottti.android.app.template.presentation.law.article.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sottti.android.app.template.domain.law.fixtures.fixtureRoubo
import com.sottti.android.app.template.presentation.law.article.data.toState
import com.sottti.android.app.template.presentation.law.article.model.ArticleDetailsState

internal class ArticleDetailsUiStateProvider : PreviewParameterProvider<ArticleDetailsState> {
    override val values: Sequence<ArticleDetailsState>
        get() = sequence {
            yield(ArticleDetailsState.Loading)
            yield(ArticleDetailsState.NotFound)
            yield(ArticleDetailsState.Loaded(fixtureRoubo.toState()))
        }
}
