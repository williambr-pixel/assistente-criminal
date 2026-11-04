package com.sottti.android.app.template.data.law.repository

import com.sottti.android.app.template.data.law.datasource.CriminalArticlesDataSource
import com.sottti.android.app.template.domain.law.model.CriminalArticle
import com.sottti.android.app.template.domain.law.model.CriminalArticleId
import com.sottti.android.app.template.domain.law.repository.LawRepository
import javax.inject.Inject

internal class LawRepositoryImpl @Inject constructor(
    private val dataSource: CriminalArticlesDataSource,
) : LawRepository {

    override suspend fun articles(): List<CriminalArticle> = dataSource.articles()

    override suspend fun article(articleId: CriminalArticleId): CriminalArticle? =
        dataSource.articles().firstOrNull { article -> article.id == articleId }
}
