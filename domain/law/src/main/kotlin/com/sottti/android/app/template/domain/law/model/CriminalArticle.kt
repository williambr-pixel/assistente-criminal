package com.sottti.android.app.template.domain.law.model

/**
 * Um tipo penal com os dados usados tanto pela busca quanto pela tela de detalhes.
 *
 * [strongKeywords] são termos que, sozinhos, praticamente identificam o tipo penal.
 * [keywords] são termos de apoio, que somam evidências mas não decidem a busca.
 * [disqualifiers] são termos que indicam que outro tipo penal é mais provável.
 */
public data class CriminalArticle(
    public val id: CriminalArticleId,
    public val code: String,
    public val article: String,
    public val title: String,
    public val group: CrimeGroup,
    public val legalText: String,
    public val penalty: String,
    public val elements: List<String>,
    public val observations: List<String>,
    public val strongKeywords: List<String>,
    public val keywords: List<String>,
    public val disqualifiers: List<String>,
)
