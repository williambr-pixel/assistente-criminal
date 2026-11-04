package com.sottti.android.app.template.domain.law.usecase

import com.sottti.android.app.template.domain.law.model.CaseMatch
import com.sottti.android.app.template.domain.law.model.CriminalArticle
import com.sottti.android.app.template.domain.law.model.MatchConfidence
import com.sottti.android.app.template.domain.law.repository.LawRepository
import com.sottti.android.app.template.domain.law.search.NormalizedCaseText
import javax.inject.Inject

public class SearchCriminalArticlesImpl @Inject constructor(
    private val repository: LawRepository,
) : SearchCriminalArticles {

    public override suspend fun invoke(description: String): List<CaseMatch> {
        val text = NormalizedCaseText(description)
        if (text.normalizedValue.isBlank()) return emptyList()

        return repository.articles()
            .mapNotNull { article -> article.match(text) }
            .sortedWith(
                compareByDescending<CaseMatch> { match -> match.confidence.ordinal }
                    .thenByDescending { match -> match.score }
                    .thenBy { match -> match.article.article }
            )
    }

    private fun CriminalArticle.match(text: NormalizedCaseText): CaseMatch? {
        val strongMatches = strongKeywords.filter(text::mentions)
        val supportMatches = keywords.filter(text::mentions)
        val conflicts = disqualifiers.filter(text::mentions)
        val score = strongMatches.size * STRONG_KEYWORD_WEIGHT +
            supportMatches.size * KEYWORD_WEIGHT -
            conflicts.size * CONFLICT_PENALTY

        if ((strongMatches.isEmpty() && supportMatches.isEmpty()) || score <= 0) return null

        return CaseMatch(
            article = this,
            confidence = confidence(
                strongMatches = strongMatches.size,
                supportMatches = supportMatches.size,
                conflicts = conflicts.size,
            ),
            score = score,
            matchedTerms = strongMatches + supportMatches,
            conflictTerms = conflicts,
        )
    }

    private fun confidence(
        strongMatches: Int,
        supportMatches: Int,
        conflicts: Int,
    ): MatchConfidence {
        val base = when {
            strongMatches >= HIGH_STRONG_MATCHES ||
                (strongMatches >= 1 && supportMatches >= HIGH_SUPPORT_WITH_STRONG) ||
                supportMatches >= HIGH_SUPPORT_MATCHES -> MatchConfidence.HIGH

            strongMatches >= 1 ||
                supportMatches >= MEDIUM_SUPPORT_MATCHES -> MatchConfidence.MEDIUM

            else -> MatchConfidence.LOW
        }

        return if (conflicts >= CONFLICTS_THAT_DOWNGRADE) base.downgraded() else base
    }

    private fun MatchConfidence.downgraded(): MatchConfidence =
        MatchConfidence.entries.getOrElse(ordinal - 1) { MatchConfidence.LOW }

    private companion object {
        private const val STRONG_KEYWORD_WEIGHT = 3
        private const val KEYWORD_WEIGHT = 1
        private const val CONFLICT_PENALTY = 2
        private const val HIGH_STRONG_MATCHES = 2
        private const val HIGH_SUPPORT_WITH_STRONG = 2
        private const val HIGH_SUPPORT_MATCHES = 4
        private const val MEDIUM_SUPPORT_MATCHES = 2
        private const val CONFLICTS_THAT_DOWNGRADE = 2
    }
}
