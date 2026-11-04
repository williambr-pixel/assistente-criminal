package com.sottti.android.app.template.presentation.law.article.data

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.sottti.android.app.template.domain.law.fixtures.fixtureRoubo
import com.sottti.android.app.template.domain.law.model.CriminalArticle
import com.sottti.android.app.template.domain.law.model.CriminalArticleId
import com.sottti.android.app.template.domain.law.usecase.GetCriminalArticle
import com.sottti.android.app.template.presentation.law.article.model.ArticleDetailsActions.NavigateBack
import com.sottti.android.app.template.presentation.law.article.model.ArticleDetailsState
import com.sottti.android.app.template.presentation.navigation.manager.NavigationManagerFake
import com.sottti.android.app.template.presentation.navigation.model.NavigationCommand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
internal class ArticleDetailsViewModelTest {

    @get:Rule
    internal val mainDispatcherRule = MainDispatcherRule()

    private val navigationManager = NavigationManagerFake()
    private val getCriminalArticle = GetCriminalArticleFake(listOf(fixtureRoubo))

    @Test
    fun `given the article exists, when the state is observed, then it loads the article`() =
        runTest {
            val viewModel = viewModel(articleId = fixtureRoubo.id)

            viewModel.state.test {
                advanceUntilIdle()

                assertThat(viewModel.state.value)
                    .isEqualTo(ArticleDetailsState.Loaded(fixtureRoubo.toState()))
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given the view model was created, when the state is read, then it is loading`() =
        runTest {
            val viewModel = viewModel(articleId = fixtureRoubo.id)

            assertThat(viewModel.state.value).isEqualTo(ArticleDetailsState.Loading)
        }

    @Test
    fun `given the article does not exist, when the state is observed, then it is not found`() =
        runTest {
            val viewModel = viewModel(articleId = CriminalArticleId(UNKNOWN_ARTICLE_ID))

            viewModel.state.test {
                advanceUntilIdle()

                assertThat(viewModel.state.value).isEqualTo(ArticleDetailsState.NotFound)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given navigate back, when the action is handled, then it navigates back`() = runTest {
        val viewModel = viewModel(articleId = fixtureRoubo.id)

        viewModel.onAction(NavigateBack)

        navigationManager.commands().test {
            assertThat(awaitItem()).isEqualTo(NavigationCommand.NavigateBack)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun viewModel(articleId: CriminalArticleId) = ArticleDetailsViewModel(
        articleId = articleId,
        navigationManager = navigationManager,
        getCriminalArticle = getCriminalArticle,
    )

    private companion object {
        private const val UNKNOWN_ARTICLE_ID = "unknown"
    }
}

private class GetCriminalArticleFake(
    private val articles: List<CriminalArticle>,
) : GetCriminalArticle {
    override suspend fun invoke(articleId: CriminalArticleId): CriminalArticle? =
        articles.firstOrNull { article -> article.id == articleId }
}

@OptIn(ExperimentalCoroutinesApi::class)
internal class MainDispatcherRule(
    private val dispatcher: TestDispatcher = StandardTestDispatcher(),
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
