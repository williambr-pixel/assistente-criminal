package com.sottti.android.app.template.di.domain.law

import com.sottti.android.app.template.domain.law.usecase.GetCriminalArticle
import com.sottti.android.app.template.domain.law.usecase.GetCriminalArticleImpl
import com.sottti.android.app.template.domain.law.usecase.SearchCriminalArticles
import com.sottti.android.app.template.domain.law.usecase.SearchCriminalArticlesImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DomainLawModule {

    @Binds
    @Singleton
    abstract fun bindSearchCriminalArticles(
        impl: SearchCriminalArticlesImpl,
    ): SearchCriminalArticles

    @Binds
    @Singleton
    abstract fun bindGetCriminalArticle(
        impl: GetCriminalArticleImpl,
    ): GetCriminalArticle
}
