package com.sottti.android.app.template.domain.law.usecase

import com.google.common.truth.Truth.assertThat
import com.sottti.android.app.template.domain.law.fixtures.fixtureAmeaca
import com.sottti.android.app.template.domain.law.fixtures.fixtureCriminalArticles
import com.sottti.android.app.template.domain.law.fixtures.fixtureFurto
import com.sottti.android.app.template.domain.law.fixtures.fixtureRoubo
import com.sottti.android.app.template.domain.law.model.CriminalArticle
import com.sottti.android.app.template.domain.law.model.CriminalArticleId
import com.sottti.android.app.template.domain.law.model.MatchConfidence
import com.sottti.android.app.template.domain.law.repository.LawRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class SearchCriminalArticlesImplTest {

    private val search = SearchCriminalArticlesImpl(
        repository = LawRepositoryFake(fixtureCriminalArticles),
    )

    @Test
    fun `returns no matches for a blank description`() = runTest {
        assertThat(search("   ")).isEmpty()
    }

    @Test
    fun `returns no matches for an unrelated description`() = runTest {
        val matches = search("Discussão sobre cláusula de contrato de aluguel comercial.")

        assertThat(matches).isEmpty()
    }

    @Test
    fun `ranks roubo first when the report mentions an armed robbery`() = runTest {
        val matches = search(
            "O indivíduo armado anunciou o assalto, rendeu a vítima e levou o celular.",
        )

        assertThat(matches).isNotEmpty()
        assertThat(matches.first().article.id).isEqualTo(fixtureRoubo.id)
        assertThat(matches.first().confidence).isEqualTo(MatchConfidence.HIGH)
        assertThat(matches.first().matchedTerms).isNotEmpty()
    }

    @Test
    fun `matches theft when the report mentions a subtração without violence`() = runTest {
        val matches = search("O autor furtou o celular da vítima dentro da loja.")

        assertThat(matches.map { match -> match.article.id }).contains(fixtureFurto.id)
    }

    @Test
    fun `matches threat even with conjugated verb and accents`() = runTest {
        val matches = search("O vizinho ameaçou a vítima de morte por mensagem.")

        assertThat(matches.map { match -> match.article.id }).contains(fixtureAmeaca.id)
    }

    @Test
    fun `penalizes an article when a disqualifier is present`() = runTest {
        val plainTheft = search("O autor furtou o celular da vítima dentro da loja.")
        val armedTheft = search("O autor furtou o celular da vítima dentro da loja com arma.")

        val plainScore = plainTheft.first { match -> match.article.id == fixtureFurto.id }.score
        val armedScore = armedTheft.firstOrNull { match -> match.article.id == fixtureFurto.id }?.score

        assertThat(armedScore ?: Int.MIN_VALUE).isLessThan(plainScore)
    }
}

internal class LawRepositoryFake(
    private val articles: List<CriminalArticle>,
) : LawRepository {
    override suspend fun articles(): List<CriminalArticle> = articles

    override suspend fun article(articleId: CriminalArticleId): CriminalArticle? =
        articles.firstOrNull { article -> article.id == articleId }
}
