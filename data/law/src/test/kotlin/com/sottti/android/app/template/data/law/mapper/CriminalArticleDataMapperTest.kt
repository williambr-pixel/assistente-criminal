package com.sottti.android.app.template.data.law.mapper

import com.google.common.truth.Truth.assertThat
import com.sottti.android.app.template.data.law.model.CriminalArticleApiModel
import com.sottti.android.app.template.domain.law.model.CrimeGroup
import org.junit.Test

internal class CriminalArticleDataMapperTest {

    @Test
    fun `maps every field to the domain model`() {
        val apiModel = CriminalArticleApiModel(
            id = "cp-155",
            code = "Código Penal",
            article = "Art. 155",
            title = "Furto",
            group = "PATRIMONIO",
            legalText = "Subtrair coisa alheia móvel.",
            penalty = "Reclusão, de 1 a 4 anos, e multa.",
            elements = listOf("Subtração de coisa alheia móvel"),
            observations = listOf("Sem violência ou grave ameaça."),
            strongKeywords = listOf("furto"),
            keywords = listOf("celular"),
            disqualifiers = listOf("arma"),
        )

        val article = apiModel.toDomain()

        assertThat(article.id.value).isEqualTo("cp-155")
        assertThat(article.code).isEqualTo("Código Penal")
        assertThat(article.article).isEqualTo("Art. 155")
        assertThat(article.title).isEqualTo("Furto")
        assertThat(article.group).isEqualTo(CrimeGroup.PATRIMONIO)
        assertThat(article.legalText).isEqualTo("Subtrair coisa alheia móvel.")
        assertThat(article.elements).containsExactly("Subtração de coisa alheia móvel")
        assertThat(article.observations).containsExactly("Sem violência ou grave ameaça.")
        assertThat(article.strongKeywords).containsExactly("furto")
        assertThat(article.keywords).containsExactly("celular")
        assertThat(article.disqualifiers).containsExactly("arma")
    }

    @Test
    fun `falls back to OUTROS when the group is unknown`() {
        val article = apiModel(group = "GRUPO_INEXISTENTE").toDomain()

        assertThat(article.group).isEqualTo(CrimeGroup.OUTROS)
    }

    private fun apiModel(group: String = "PATRIMONIO") = CriminalArticleApiModel(
        id = "id",
        code = "Código Penal",
        article = "Art. 1",
        title = "Título",
        group = group,
        legalText = "Texto",
        penalty = "Pena",
    )
}
