package com.sottti.android.app.template.presentation.law.search.data

import com.google.common.truth.Truth.assertThat
import com.sottti.android.app.template.domain.law.fixtures.fixtureFurto
import com.sottti.android.app.template.domain.law.fixtures.fixtureRoubo
import com.sottti.android.app.template.domain.law.model.CaseMatch
import com.sottti.android.app.template.domain.law.model.MatchConfidence
import com.sottti.android.app.template.presentation.law.search.model.CaseMatchState
import org.junit.Test

internal class CaseSearchMapperTest {

    @Test
    fun `maps a match to the ui model`() {
        val match = CaseMatch(
            article = fixtureRoubo,
            confidence = MatchConfidence.HIGH,
            score = 9,
            matchedTerms = listOf("roubo", "arma"),
            conflictTerms = emptyList(),
        )

        assertThat(match.toUi()).isEqualTo(
            CaseMatchState(
                id = fixtureRoubo.id.value,
                code = fixtureRoubo.code,
                article = fixtureRoubo.article,
                title = fixtureRoubo.title,
                confidence = MatchConfidence.HIGH,
                matchedTerms = listOf("roubo", "arma"),
                hasConflicts = false,
            )
        )
    }

    @Test
    fun `flags a match with conflict terms`() {
        val match = CaseMatch(
            article = fixtureFurto,
            confidence = MatchConfidence.MEDIUM,
            score = 4,
            matchedTerms = listOf("furtou"),
            conflictTerms = listOf("arma"),
        )

        assertThat(match.toUi().hasConflicts).isTrue()
    }

    @Test
    fun `keeps the original order when mapping a list of matches`() {
        val matches = listOf(
            CaseMatch(fixtureRoubo, MatchConfidence.HIGH, 9, listOf("roubo"), emptyList()),
            CaseMatch(fixtureFurto, MatchConfidence.MEDIUM, 4, listOf("furto"), emptyList()),
        )

        assertThat(matches.toUi().map(CaseMatchState::id))
            .containsExactly(fixtureRoubo.id.value, fixtureFurto.id.value)
            .inOrder()
    }
}
