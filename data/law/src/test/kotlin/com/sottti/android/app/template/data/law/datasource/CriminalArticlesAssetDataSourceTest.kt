package com.sottti.android.app.template.data.law.datasource

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
internal class CriminalArticlesAssetDataSourceTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val dataSource = CriminalArticlesAssetDataSource(
        context = context,
        json = Json { ignoreUnknownKeys = true },
    )

    @Test
    fun `loads every article from the asset`() = runBlocking {
        assertThat(dataSource.articles()).hasSize(EXPECTED_ARTICLES)
    }

    @Test
    fun `loads articles with unique ids`() = runBlocking {
        val ids = dataSource.articles().map { article -> article.id.value }

        assertThat(ids).containsNoDuplicates()
    }

    @Test
    fun `maps the asset entries to domain models`() = runBlocking {
        val article = dataSource.articles().first { article -> article.id.value == ROBBERY_ID }

        assertThat(article.article).isEqualTo("Art. 157")
        assertThat(article.title).isEqualTo("Roubo")
        assertThat(article.elements).isNotEmpty()
        assertThat(article.strongKeywords).contains("roubo")
    }

    @Test
    fun `keeps the parsed articles cached between calls`() = runBlocking {
        val first = dataSource.articles()
        val second = dataSource.articles()

        assertThat(second).isSameInstanceAs(first)
    }

    private companion object {
        private const val EXPECTED_ARTICLES = 53
        private const val ROBBERY_ID = "cp-157"
    }
}
