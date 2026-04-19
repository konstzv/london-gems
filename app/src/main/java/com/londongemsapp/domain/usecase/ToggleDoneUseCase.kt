package com.londongemsapp.domain.usecase

import com.londongemsapp.domain.repository.RecommendationRepository
import javax.inject.Inject

class ToggleDoneUseCase @Inject constructor(
    private val repository: RecommendationRepository
) {
    suspend operator fun invoke(redditId: String) {
        repository.toggleDone(redditId)
    }
}
