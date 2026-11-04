package com.sottti.android.app.template.presentation.law.article.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.sottti.android.app.template.presentation.design.system.colors.color.composition.local.colors
import com.sottti.android.app.template.presentation.design.system.dimensions.composition.local.dimensions
import com.sottti.android.app.template.presentation.design.system.progress.indicators.ProgressIndicator
import com.sottti.android.app.template.presentation.design.system.shapes.composition.local.shapes
import com.sottti.android.app.template.presentation.design.system.text.Text
import com.sottti.android.app.template.presentation.design.system.top.bars.ui.MainTopBar
import com.sottti.android.app.template.presentation.law.article.R
import com.sottti.android.app.template.presentation.law.article.model.ArticleDetailsActions
import com.sottti.android.app.template.presentation.law.article.model.ArticleDetailsActions.NavigateBack
import com.sottti.android.app.template.presentation.law.article.model.ArticleDetailsState
import com.sottti.android.app.template.presentation.law.article.model.ArticleDetailsState.Loaded
import com.sottti.android.app.template.presentation.law.article.model.ArticleDetailsState.Loading
import com.sottti.android.app.template.presentation.law.article.model.ArticleDetailsState.NotFound
import com.sottti.android.app.template.presentation.law.article.model.ArticleState

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun ArticleDetailsContent(
    state: ArticleDetailsState,
    onAction: (ArticleDetailsActions) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MainTopBar(
                title = (state as? Loaded)?.article?.article,
                onNavigationIconClick = { onAction(NavigateBack) },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { padding ->
        when (state) {
            Loading -> ProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )

            NotFound -> NotFoundArticle(modifier = Modifier.padding(padding))
            is Loaded -> ArticleStateContent(
                article = state.article,
                padding = padding,
                scrollBehavior = scrollBehavior,
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ArticleStateContent(
    article: ArticleState,
    padding: PaddingValues,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        contentPadding = PaddingValues(dimensions.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(dimensions.spacing.mediumLarge),
    ) {
        item(key = "header") { Header(article = article) }
        item(key = "legal text") {
            DetailsSection(header = R.string.article_details_legal_text_header) {
                Text.Body.Medium(text = article.legalText, textColor = colors.onSurface)
            }
        }
        item(key = "penalty") {
            DetailsSection(header = R.string.article_details_penalty_header) {
                Text.Body.Medium(text = article.penalty, textColor = colors.onSurface)
            }
        }
        item(key = "elements") {
            DetailsSection(header = R.string.article_details_elements_header) {
                BulletList(items = article.elements)
            }
        }
        item(key = "observations") {
            DetailsSection(header = R.string.article_details_observations_header) {
                BulletList(items = article.observations)
            }
        }
        item(key = "disclaimer") {
            Text.Body.Small(
                text = stringResource(R.string.article_details_disclaimer),
                textColor = colors.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun Header(article: ArticleState) {
    Column(verticalArrangement = Arrangement.spacedBy(dimensions.spacing.small)) {
        Text.Headline.Medium(text = article.article, textColor = colors.onBackground)
        Text.Title.Large(text = article.title, textColor = colors.onBackground)
        Text.Body.Medium(text = article.code, textColor = colors.onSurfaceVariant)
        GroupBadge(groupResId = article.groupResId)
    }
}

@Composable
private fun GroupBadge(@StringRes groupResId: Int) {
    Surface(color = colors.secondaryContainer, shape = shapes.roundedCorner.small) {
        Text.Label.Medium(
            text = stringResource(groupResId),
            textColor = colors.onSecondaryContainer,
            modifier = Modifier.padding(
                horizontal = dimensions.spacing.smallMedium,
                vertical = dimensions.spacing.extraSmall,
            ),
        )
    }
}

@Composable
private fun DetailsSection(
    @StringRes header: Int,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(dimensions.spacing.small)) {
        Text.Label.Large(text = stringResource(header), textColor = colors.onBackground)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensions.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(dimensions.spacing.small),
                content = content,
            )
        }
    }
}

@Composable
private fun BulletList(items: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(dimensions.spacing.small)) {
        items.forEach { item -> BulletItem(text = item) }
    }
}

@Composable
private fun BulletItem(text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(dimensions.spacing.small)) {
        Text.Body.Medium(text = BULLET, textColor = colors.primary)
        Text.Body.Medium(text = text, textColor = colors.onSurface)
    }
}

@Composable
private fun NotFoundArticle(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensions.spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text.Body.Large(
            text = stringResource(R.string.article_details_not_found),
            textColor = colors.onBackground,
            textAlign = TextAlign.Center,
        )
    }
}

private const val BULLET = "\u2022"
