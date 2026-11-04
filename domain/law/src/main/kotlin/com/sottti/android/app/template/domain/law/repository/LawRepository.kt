package com.sottti.android.app.template.domain.law.repository

import com.sottti.android.app.template.domain.law.model.CriminalArticle
import com.sottti.android.app.template.domain.law.model.CriminalArticleId

public interface LawRepository {
    public suspend fun articles(): List<CriminalArticle>
    public suspend fun article(articleId: CriminalArticleId): CriminalArticle?
}
