package com.londongemsapp.di

import com.londongemsapp.data.repository.RecommendationRepositoryImpl
import com.londongemsapp.domain.classifier.CategoryClassifier
import com.londongemsapp.domain.classifier.KeywordCategoryClassifier
import com.londongemsapp.domain.repository.RecommendationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRecommendationRepository(
        impl: RecommendationRepositoryImpl
    ): RecommendationRepository

    @Binds
    @Singleton
    abstract fun bindCategoryClassifier(
        impl: KeywordCategoryClassifier
    ): CategoryClassifier
}
