package com.sottti.android.app.template.domain.law.usecase

import com.sottti.android.app.template.domain.law.model.CriminalArticle
import com.sottti.android.app.template.domain.law.model.CriminalArticleId

public fun interface GetCriminalArticle {
    public suspend operator fun invoke(articleId: CriminalArticleId): CriminalArticle?
}
