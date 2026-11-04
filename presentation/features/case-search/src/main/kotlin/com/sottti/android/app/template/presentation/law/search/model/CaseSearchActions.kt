package com.sottti.android.app.template.presentation.law.search.model

import androidx.compose.runtime.Immutable

@Immutable
internal sealed interface CaseSearchActions {

    @Immutable
    data class UpdateQuery(val query: String) : CaseSearchActions

    @Immutable
    data class UseExample(val description: String) : CaseSearchActions

    @Immutable
    data class ShowArticleDetail(val articleId: String) : CaseSearchActions

    @Immutable
    data object Search : CaseSearchActions

    @Immutable
    data object ClearQuery : CaseSearchActions
}
