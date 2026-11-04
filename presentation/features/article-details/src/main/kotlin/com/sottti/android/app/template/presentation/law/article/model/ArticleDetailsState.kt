package com.sottti.android.app.template.presentation.law.article.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
internal sealed interface ArticleDetailsState {

    @Immutable
    data object Loading : ArticleDetailsState

    @Immutable
    data object NotFound : ArticleDetailsState

    @Immutable
    data class Loaded(val article: ArticleState) : ArticleDetailsState
}

@Immutable
internal data class ArticleState(
    val article: String,
    val title: String,
    val code: String,
    @StringRes val groupResId: Int,
    val legalText: String,
    val penalty: String,
    val elements: List<String>,
    val observations: List<String>,
)
