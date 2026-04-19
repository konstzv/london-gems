package com.londongemsapp.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.londongemsapp.domain.model.DataResult
import com.londongemsapp.domain.usecase.SyncRecommendationsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val syncRecommendations: SyncRecommendationsUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return when (syncRecommendations()) {
            is DataResult.Success -> Result.success()
            is DataResult.Error -> Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "sync_recommendations"
    }
}
