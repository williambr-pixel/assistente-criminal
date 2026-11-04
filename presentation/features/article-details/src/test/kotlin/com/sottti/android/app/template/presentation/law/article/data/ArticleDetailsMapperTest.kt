package com.sottti.android.app.template.presentation.law.article.data

import com.google.common.truth.Truth.assertThat
import com.sottti.android.app.template.domain.law.fixtures.fixtureRoubo
import com.sottti.android.app.template.domain.law.model.CrimeGroup
import com.sottti.android.app.template.presentation.law.article.R
import com.sottti.android.app.template.presentation.law.article.model.ArticleState
import org.junit.Test

internal class ArticleDetailsMapperTest {

    @Test
    fun `maps every article field to the ui state`() {
        assertThat(fixtureRoubo.toState()).isEqualTo(
            ArticleState(
                article = fixtureRoubo.article,
                title = fixtureRoubo.title,
                code = fixtureRoubo.code,
                groupResId = R.string.crime_group_patrimonio,
                legalText = fixtureRoubo.legalText,
                penalty = fixtureRoubo.penalty,
                elements = fixtureRoubo.elements,
                observations = fixtureRoubo.observations,
            )
        )
    }

    @Test
    fun `maps every crime group to its string resource`() {
        val expected = mapOf(
            CrimeGroup.ADMINISTRACAO_PUBLICA to R.string.crime_group_administracao_publica,
            CrimeGroup.ARMAS to R.string.crime_group_armas,
            CrimeGroup.DIGNIDADE_SEXUAL to R.string.crime_group_dignidade_sexual,
            CrimeGroup.DROGAS to R.string.crime_group_drogas,
            CrimeGroup.FE_PUBLICA to R.string.crime_group_fe_publica,
            CrimeGroup.HONRA to R.string.crime_group_honra,
            CrimeGroup.LIBERDADE to R.string.crime_group_liberdade,
            CrimeGroup.OUTROS to R.string.crime_group_outros,
            CrimeGroup.PATRIMONIO to R.string.crime_group_patrimonio,
            CrimeGroup.TRANSITO to R.string.crime_group_transito,
            CrimeGroup.VIDA to R.string.crime_group_vida,
            CrimeGroup.VIOLENCIA_DOMESTICA to R.string.crime_group_violencia_domestica,
        )

        assertThat(expected.keys).containsExactlyElementsIn(CrimeGroup.entries)
        expected.forEach { (group, resId) ->
            assertThat(group.labelRes()).isEqualTo(resId)
        }
    }
}
