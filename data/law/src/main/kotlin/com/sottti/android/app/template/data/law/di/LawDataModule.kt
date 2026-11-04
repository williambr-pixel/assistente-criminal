package com.sottti.android.app.template.data.law.di

import com.sottti.android.app.template.data.law.datasource.CriminalArticlesAssetDataSource
import com.sottti.android.app.template.data.law.datasource.CriminalArticlesDataSource
import com.sottti.android.app.template.data.law.repository.LawRepositoryImpl
import com.sottti.android.app.template.domain.law.repository.LawRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class LawDataModule {

    @Binds
    @Singleton
    abstract fun bindDataSource(
        impl: CriminalArticlesAssetDataSource,
    ): CriminalArticlesDataSource

    @Binds
    @Singleton
    abstract fun bindRepository(
        impl: LawRepositoryImpl,
    ): LawRepository

    companion object {
        @Provides
        @Singleton
        fun provideJson(): Json = Json {
            ignoreUnknownKeys = true
        }
    }
}
