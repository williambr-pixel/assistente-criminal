package com.sottti.android.app.template.data.law.datasource

import android.content.Context
import com.sottti.android.app.template.data.law.mapper.toDomain
import com.sottti.android.app.template.data.law.model.CriminalArticleApiModel
import com.sottti.android.app.template.domain.law.model.CriminalArticle
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

internal interface CriminalArticlesDataSource {
    suspend fun articles(): List<CriminalArticle>
}

internal class CriminalArticlesAssetDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json,
) : CriminalArticlesDataSource {

    private val mutex = Mutex()
    private var cache: List<CriminalArticle>? = null

    override suspend fun articles(): List<CriminalArticle> = mutex.withLock {
        cache ?: withContext(Dispatchers.IO) { readArticles() }.also { articles -> cache = articles }
    }

    private fun readArticles(): List<CriminalArticle> =
        context.assets.open(ASSET_FILE_NAME).bufferedReader().use { reader ->
            json.decodeFromString<List<CriminalArticleApiModel>>(reader.readText())
                .map(CriminalArticleApiModel::toDomain)
        }

    private companion object {
        private const val ASSET_FILE_NAME = "criminal_articles.json"
    }
}
