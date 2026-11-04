package com.sottti.android.app.template.presentation.navigation.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.sottti.android.app.template.presentation.character.details.ui.CharacterDetails
import com.sottti.android.app.template.presentation.characters.list.ui.CharactersList
import com.sottti.android.app.template.presentation.law.article.ui.ArticleDetails
import com.sottti.android.app.template.presentation.law.search.ui.CaseSearch
import com.sottti.android.app.template.presentation.navigation.model.NavigationDestination
import com.sottti.android.app.template.presentation.navigation.model.NavigationDestination.ArticleDetail
import com.sottti.android.app.template.presentation.navigation.model.NavigationDestination.CaseSearch
import com.sottti.android.app.template.presentation.navigation.model.NavigationDestination.CharacterDetail
import com.sottti.android.app.template.presentation.navigation.model.NavigationDestination.CharactersList

@Composable
@ReadOnlyComposable
internal fun navigationEntries(): EntryProvider<NavKey> = { key ->
    when (key as NavigationDestination) {
        CaseSearch -> NavEntry(key) { CaseSearch() }
        is ArticleDetail -> NavEntry(key = key) {
            val articleId: String = (key as ArticleDetail).articleId
            ArticleDetails(articleId)
        }

        CharactersList -> NavEntry(key) { CharactersList() }
        is CharacterDetail -> NavEntry(key = key) {
            val characterId: Int = (key as CharacterDetail).characterId
            CharacterDetails(characterId)
        }
    }
}
