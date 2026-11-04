package com.sottti.android.app.template.presentation.navigation.impl.fakes


import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.sottti.android.app.template.presentation.navigation.impl.EntryProvider
import com.sottti.android.app.template.presentation.navigation.impl.TaggedComposable
import com.sottti.android.app.template.presentation.navigation.model.NavigationDestination
import com.sottti.android.app.template.presentation.navigation.model.NavigationDestination.ArticleDetail
import com.sottti.android.app.template.presentation.navigation.model.NavigationDestination.CaseSearch
import com.sottti.android.app.template.presentation.navigation.model.NavigationDestination.CharacterDetail
import com.sottti.android.app.template.presentation.navigation.model.NavigationDestination.CharactersList

internal const val ARTICLE_DETAIL_FEATURE_TEST_TAG = "articleDetailFeatureTestTag"
internal const val CASE_SEARCH_FEATURE_TEST_TAG = "caseSearchFeatureTestTag"
internal const val CHARACTER_DETAIL_FEATURE_TEST_TAG = "characterDetailFeatureTestTag"
internal const val CHARACTERS_LIST_FEATURE_TEST_TAG = "charactersListFeatureTestTag"

internal fun navigationEntriesFake(): EntryProvider<NavKey> = { key ->
    when (key as NavigationDestination) {
        CaseSearch -> NavEntry(key) { TaggedComposable(CASE_SEARCH_FEATURE_TEST_TAG) }
        is ArticleDetail -> NavEntry(key) { TaggedComposable(ARTICLE_DETAIL_FEATURE_TEST_TAG) }
        CharactersList -> NavEntry(key) { TaggedComposable(CHARACTERS_LIST_FEATURE_TEST_TAG) }
        is CharacterDetail -> NavEntry(key) { TaggedComposable(CHARACTER_DETAIL_FEATURE_TEST_TAG) }
    }
}
