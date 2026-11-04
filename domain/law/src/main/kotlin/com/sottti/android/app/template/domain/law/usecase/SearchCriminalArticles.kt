package com.sottti.android.app.template.domain.law.usecase

import com.sottti.android.app.template.domain.law.model.CaseMatch

public fun interface SearchCriminalArticles {
    public suspend operator fun invoke(description: String): List<CaseMatch>
}
