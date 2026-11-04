package com.sottti.android.app.template.presentation.law.search.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sottti.android.app.template.domain.law.usecase.SearchCriminalArticles
import com.sottti.android.app.template.presentation.law.search.R
import com.sottti.android.app.template.presentation.law.search.di.CaseSearchTestScope
import com.sottti.android.app.template.presentation.law.search.model.CaseSearchActions
import com.sottti.android.app.template.presentation.law.search.model.CaseSearchActions.ClearQuery
import com.sottti.android.app.template.presentation.law.search.model.CaseSearchActions.Search
import com.sottti.android.app.template.presentation.law.search.model.CaseSearchActions.ShowArticleDetail
import com.sottti.android.app.template.presentation.law.search.model.CaseSearchActions.UpdateQuery
import com.sottti.android.app.template.presentation.law.search.model.CaseSearchActions.UseExample
import com.sottti.android.app.template.presentation.law.search.model.CaseSearchState
import com.sottti.android.app.template.presentation.navigation.manager.NavigationManager
import com.sottti.android.app.template.presentation.navigation.model.NavigationDestination.ArticleDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class CaseSearchViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
    private val searchCriminalArticles: SearchCriminalArticles,
    @CaseSearchTestScope testScope: CoroutineScope? = null,
) : ViewModel() {

    private val scope: CoroutineScope = testScope ?: viewModelScope

    private val mutableState = MutableStateFlow(
        CaseSearchState(titleResId = R.string.case_search_title)
    )

    val state: StateFlow<CaseSearchState> = mutableState.asStateFlow()

    internal val onAction: (CaseSearchActions) -> Unit = ::processAction

    private fun processAction(action: CaseSearchActions) {
        when (action) {
            is UpdateQuery -> mutableState.update { state -> state.copy(query = action.query) }
            is UseExample -> {
                mutableState.update { state -> state.copy(query = action.description) }
                search()
            }

            Search -> search()
            ClearQuery -> mutableState.update { state ->
                state.copy(query = "", hasSearched = false, results = emptyList())
            }

            is ShowArticleDetail -> navigationManager.navigateTo(
                ArticleDetail(articleId = action.articleId)
            )
        }
    }

    private fun search() {
        val description = mutableState.value.query
        if (description.isBlank()) return

        scope.launch {
            mutableState.update { state -> state.copy(isSearching = true, hasSearched = true) }

            val results = searchCriminalArticles(description).toUi()

            mutableState.update { state ->
                state.copy(isSearching = false, results = results)
            }
        }
    }
}
