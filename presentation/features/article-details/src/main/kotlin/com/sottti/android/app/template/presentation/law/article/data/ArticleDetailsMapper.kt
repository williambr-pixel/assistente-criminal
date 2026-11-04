package com.sottti.android.app.template.presentation.law.article.data

import androidx.annotation.StringRes
import com.sottti.android.app.template.domain.law.model.CrimeGroup
import com.sottti.android.app.template.domain.law.model.CriminalArticle
import com.sottti.android.app.template.presentation.law.article.R
import com.sottti.android.app.template.presentation.law.article.model.ArticleState

internal fun CriminalArticle.toState(): ArticleState = ArticleState(
    article = article,
    title = title,
    code = code,
    groupResId = group.labelRes(),
    legalText = legalText,
    penalty = penalty,
    elements = elements,
    observations = observations,
)

@StringRes
internal fun CrimeGroup.labelRes(): Int = when (this) {
    CrimeGroup.ADMINISTRACAO_PUBLICA -> R.string.crime_group_administracao_publica
    CrimeGroup.ARMAS -> R.string.crime_group_armas
    CrimeGroup.DIGNIDADE_SEXUAL -> R.string.crime_group_dignidade_sexual
    CrimeGroup.DROGAS -> R.string.crime_group_drogas
    CrimeGroup.FE_PUBLICA -> R.string.crime_group_fe_publica
    CrimeGroup.HONRA -> R.string.crime_group_honra
    CrimeGroup.LIBERDADE -> R.string.crime_group_liberdade
    CrimeGroup.OUTROS -> R.string.crime_group_outros
    CrimeGroup.PATRIMONIO -> R.string.crime_group_patrimonio
    CrimeGroup.TRANSITO -> R.string.crime_group_transito
    CrimeGroup.VIDA -> R.string.crime_group_vida
    CrimeGroup.VIOLENCIA_DOMESTICA -> R.string.crime_group_violencia_domestica
}
