package com.londongemsapp.domain.usecase

import com.londongemsapp.domain.model.DataResult
import com.londongemsapp.domain.repository.RecommendationRepository
import javax.inject.Inject

class SyncRecommendationsUseCase @Inject constructor(
    private val repository: RecommendationRepository
) {
    suspend operator fun invoke(): DataResult<Int> {
        return repository.syncFromReddit()
    }
}
