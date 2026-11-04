package com.sottti.android.app.template.data.law.mapper

import com.sottti.android.app.template.data.law.model.CriminalArticleApiModel
import com.sottti.android.app.template.domain.law.model.CrimeGroup
import com.sottti.android.app.template.domain.law.model.CriminalArticle
import com.sottti.android.app.template.domain.law.model.CriminalArticleId

internal fun CriminalArticleApiModel.toDomain(): CriminalArticle = CriminalArticle(
    id = CriminalArticleId(id),
    code = code,
    article = article,
    title = title,
    group = group.toCrimeGroup(),
    legalText = legalText,
    penalty = penalty,
    elements = elements,
    observations = observations,
    strongKeywords = strongKeywords,
    keywords = keywords,
    disqualifiers = disqualifiers,
)

private fun String.toCrimeGroup(): CrimeGroup =
    CrimeGroup.entries.firstOrNull { group -> group.name == this } ?: CrimeGroup.OUTROS
