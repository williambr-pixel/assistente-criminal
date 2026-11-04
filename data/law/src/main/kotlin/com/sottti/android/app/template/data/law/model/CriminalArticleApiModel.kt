package com.sottti.android.app.template.data.law.model

import kotlinx.serialization.Serializable

@Serializable
internal data class CriminalArticleApiModel(
    val id: String,
    val code: String,
    val article: String,
    val title: String,
    val group: String,
    val legalText: String,
    val penalty: String,
    val elements: List<String> = emptyList(),
    val observations: List<String> = emptyList(),
    val strongKeywords: List<String> = emptyList(),
    val keywords: List<String> = emptyList(),
    val disqualifiers: List<String> = emptyList(),
)
