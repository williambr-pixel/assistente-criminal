package com.sottti.android.app.template.domain.law.model

/**
 * Resultado da análise de um relato de caso para um tipo penal específico.
 */
public data class CaseMatch(
    public val article: CriminalArticle,
    public val confidence: MatchConfidence,
    public val score: Int,
    public val matchedTerms: List<String>,
    public val conflictTerms: List<String>,
)
