package com.sottti.android.app.template.data.law.repository

import com.google.common.truth.Truth.assertThat
import com.sottti.android.app.template.data.law.datasource.CriminalArticlesDataSource
import com.sottti.android.app.template.domain.law.fixtures.fixtureCriminalArticles
import com.sottti.android.app.template.domain.law.fixtures.fixtureFurto
import com.sottti.android.app.template.domain.law.model.CriminalArticle
import com.sottti.android.app.template.domain.law.model.CriminalArticleId
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class LawRepositoryImplTest {

    private val repository = LawRepositoryImpl(
        dataSource = CriminalArticlesDataSourceFake(fixtureCriminalArticles),
    )

    @Test
    fun `returns every article from the data source`() = runTest {
        assertThat(repository.articles()).hasSize(fixtureCriminalArticles.size)
    }

    @Test
    fun `finds an article by id`() = runTest {
        val article = repository.article(fixtureFurto.id)

        assertThat(article).isEqualTo(fixtureFurto)
    }

    @Test
    fun `returns null when the id is unknown`() = runTest {
        val article = repository.article(CriminalArticleId("unknown"))

        assertThat(article).isNull()
    }
}

internal class CriminalArticlesDataSourceFake(
    private val articles: List<CriminalArticle>,
) : CriminalArticlesDataSource {
    override suspend fun articles(): List<CriminalArticle> = articles
}
