package com.sottti.android.app.template.domain.law.usecase

import com.sottti.android.app.template.domain.law.model.CriminalArticle
import com.sottti.android.app.template.domain.law.model.CriminalArticleId
import com.sottti.android.app.template.domain.law.repository.LawRepository
import javax.inject.Inject

public class GetCriminalArticleImpl @Inject constructor(
    private val repository: LawRepository,
) : GetCriminalArticle {

    public override suspend fun invoke(articleId: CriminalArticleId): CriminalArticle? =
        repository.article(articleId)
}
