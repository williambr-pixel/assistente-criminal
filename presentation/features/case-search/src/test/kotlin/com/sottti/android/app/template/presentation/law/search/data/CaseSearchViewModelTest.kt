package com.sottti.android.app.template.presentation.law.search.data

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.sottti.android.app.template.domain.law.fixtures.fixtureFurto
import com.sottti.android.app.template.domain.law.fixtures.fixtureRoubo
import com.sottti.android.app.template.domain.law.model.CaseMatch
import com.sottti.android.app.template.domain.law.model.MatchConfidence
import com.sottti.android.app.template.domain.law.usecase.SearchCriminalArticles
import com.sottti.android.app.template.presentation.law.search.model.CaseSearchActions.ClearQuery
import com.sottti.android.app.template.presentation.law.search.model.CaseSearchActions.Search
import com.sottti.android.app.template.presentation.law.search.model.CaseSearchActions.ShowArticleDetail
import com.sottti.android.app.template.presentation.law.search.model.CaseSearchActions.UpdateQuery
import com.sottti.android.app.template.presentation.navigation.manager.NavigationManagerFake
import com.sottti.android.app.template.presentation.navigation.model.NavigationCommand
import com.sottti.android.app.template.presentation.navigation.model.NavigationDestination.ArticleDetail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class CaseSearchViewModelTest {

    private val navigationManager = NavigationManagerFake()
    private val matches = listOf(
        CaseMatch(
            article = fixtureRoubo,
            confidence = MatchConfidence.HIGH,
            score = 9,
            matchedTerms = listOf("assalto", "arma"),
            conflictTerms = emptyList(),
        ),
        CaseMatch(
            article = fixtureFurto,
            confidence = MatchConfidence.MEDIUM,
            score = 4,
            matchedTerms = listOf("furtou"),
            conflictTerms = listOf("arma"),
        ),
    )
    private val searchCriminalArticles = SearchCriminalArticlesFake(matches)

    @Test
    fun `updating the query keeps it in the state`() = runTest {
        val viewModel = viewModel()

        viewModel.onAction(UpdateQuery(query = "O autor furtou o celular."))

        assertThat(viewModel.state.value.query).isEqualTo("O autor furtou o celular.")
        assertThat(viewModel.state.value.hasSearched).isFalse()
    }

    @Test
    fun `searching a blank query does not query the use case`() = runTest {
        val viewModel = viewModel()
        viewModel.onAction(UpdateQuery(query = "   "))

        viewModel.onAction(Search)
        advanceUntilIdle()

        assertThat(searchCriminalArticles.calls).isEmpty()
        assertThat(viewModel.state.value.hasSearched).isFalse()
    }

    @Test
    fun `searching maps the matches to ui models`() = runTest {
        val viewModel = viewModel()
        viewModel.onAction(UpdateQuery(query = "O autor, armado, rendeu a vítima."))

        viewModel.onAction(Search)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.hasSearched).isTrue()
        assertThat(state.isSearching).isFalse()
        assertThat(state.results).isEqualTo(matches.toUi())
        assertThat(state.results.first().confidence).isEqualTo(MatchConfidence.HIGH)
        assertThat(state.results.last().hasConflicts).isTrue()
    }

    @Test
    fun `clearing the query resets the search`() = runTest {
        val viewModel = viewModel()
        viewModel.onAction(UpdateQuery(query = "O autor, armado, rendeu a vítima."))
        viewModel.onAction(Search)
        advanceUntilIdle()

        viewModel.onAction(ClearQuery)

        assertThat(viewModel.state.value.query).isEmpty()
        assertThat(viewModel.state.value.hasSearched).isFalse()
        assertThat(viewModel.state.value.results).isEmpty()
    }

    @Test
    fun `showing an article detail navigates to ArticleDetail`() = runTest {
        val viewModel = viewModel()

        viewModel.onAction(ShowArticleDetail(articleId = fixtureRoubo.id.value))

        navigationManager.commands().test {
            assertThat(awaitItem()).isEqualTo(
                NavigationCommand.NavigateTo(ArticleDetail(articleId = fixtureRoubo.id.value))
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun CoroutineScope.viewModel() = CaseSearchViewModel(
        navigationManager = navigationManager,
        searchCriminalArticles = searchCriminalArticles,
        testScope = this,
    )
}

private class SearchCriminalArticlesFake(
    private val matches: List<CaseMatch>,
) : SearchCriminalArticles {
    val calls = mutableListOf<String>()

    override suspend fun invoke(description: String): List<CaseMatch> {
        calls += description
        return matches
    }
}
