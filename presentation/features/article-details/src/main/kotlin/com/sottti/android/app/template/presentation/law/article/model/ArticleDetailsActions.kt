package com.sottti.android.app.template.presentation.law.article.model

import androidx.compose.runtime.Immutable

@Immutable
internal sealed interface ArticleDetailsActions {

    @Immutable
    data object NavigateBack : ArticleDetailsActions
}
