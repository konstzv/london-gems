package com.londongemsapp.domain.usecase

import com.londongemsapp.domain.model.Category
import com.londongemsapp.domain.model.Recommendation
import com.londongemsapp.domain.repository.RecommendationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecommendationsUseCase @Inject constructor(
    private val repository: RecommendationRepository
) {
    operator fun invoke(category: Category? = null): Flow<List<Recommendation>> {
        return repository.getRecommendations(category)
    }
}
