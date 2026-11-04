package com.sottti.android.app.template.presentation.law.search.data

import com.sottti.android.app.template.domain.law.model.CaseMatch
import com.sottti.android.app.template.presentation.law.search.model.CaseMatchState

internal fun List<CaseMatch>.toUi(): List<CaseMatchState> = map(CaseMatch::toUi)

internal fun CaseMatch.toUi(): CaseMatchState = CaseMatchState(
    id = article.id.value,
    code = article.code,
    article = article.article,
    title = article.title,
    confidence = confidence,
    matchedTerms = matchedTerms,
    hasConflicts = conflictTerms.isNotEmpty(),
)
