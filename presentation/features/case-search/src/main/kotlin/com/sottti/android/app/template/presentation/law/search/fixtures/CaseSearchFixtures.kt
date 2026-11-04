package com.sottti.android.app.template.presentation.law.search.fixtures

import com.sottti.android.app.template.domain.law.fixtures.fixtureFurto
import com.sottti.android.app.template.domain.law.fixtures.fixtureRoubo
import com.sottti.android.app.template.domain.law.model.CaseMatch
import com.sottti.android.app.template.domain.law.model.MatchConfidence
import com.sottti.android.app.template.presentation.law.search.data.toUi
import com.sottti.android.app.template.presentation.law.search.model.CaseMatchState

internal val fixtureCaseMatchHigh: CaseMatchState = CaseMatch(
    article = fixtureRoubo,
    confidence = MatchConfidence.HIGH,
    score = 9,
    matchedTerms = listOf("assalto", "arma", "rendeu", "celular"),
    conflictTerms = emptyList(),
).toUi()

internal val fixtureCaseMatchMedium: CaseMatchState = CaseMatch(
    article = fixtureFurto,
    confidence = MatchConfidence.MEDIUM,
    score = 4,
    matchedTerms = listOf("furtou", "celular"),
    conflictTerms = listOf("arma"),
).toUi()

internal val fixtureCaseSearchResults: List<CaseMatchState> = listOf(
    fixtureCaseMatchHigh,
    fixtureCaseMatchMedium,
)
