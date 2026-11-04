package com.sottti.android.app.template.domain.law.model

/**
 * Quão segura é a correlação entre o relato do caso e o tipo penal.
 *
 * A ordem das constantes é significativa: quanto maior o ordinal, maior a confiança.
 */
public enum class MatchConfidence {
    LOW,
    MEDIUM,
    HIGH,
}
