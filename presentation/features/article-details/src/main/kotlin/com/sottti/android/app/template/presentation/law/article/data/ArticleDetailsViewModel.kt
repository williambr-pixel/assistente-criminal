package com.sottti.android.app.template.presentation.law.article.data

import androidx.lifecycle.ViewModel
import com.sottti.android.app.template.domain.law.model.CriminalArticleId
import com.sottti.android.app.template.domain.law.usecase.GetCriminalArticle
import com.sottti.android.app.template.presentation.law.article.model.ArticleDetailsActions
import com.sottti.android.app.template.presentation.law.article.model.ArticleDetailsActions.NavigateBack
import com.sottti.android.app.template.presentation.law.article.model.ArticleDetailsState
import com.sottti.android.app.template.presentation.navigation.manager.NavigationManager
import com.sottti.android.app.template.presentation.utils.stateInWhileSubscribed
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

@HiltViewModel(assistedFactory = ArticleDetailsViewModel.Factory::class)
internal class ArticleDetailsViewModel @AssistedInject constructor(
    @Assisted private val articleId: CriminalArticleId,
    private val navigationManager: NavigationManager,
    getCriminalArticle: GetCriminalArticle,
) : ViewModel() {

    @AssistedFactory
    fun interface Factory {
        fun create(articleId: CriminalArticleId): ArticleDetailsViewModel
    }

    val state: StateFlow<ArticleDetailsState> = flow { emit(getCriminalArticle(articleId)) }
        .map { article ->
            article?.let { it.toState() }?.let(ArticleDetailsState::Loaded) ?: ArticleDetailsState.NotFound
        }
        .stateInWhileSubscribed(initialValue = ArticleDetailsState.Loading)

    internal val onAction: (ArticleDetailsActions) -> Unit = ::processAction

    private fun processAction(action: ArticleDetailsActions) {
        when (action) {
            NavigateBack -> navigationManager.navigateBack()
        }
    }
}
