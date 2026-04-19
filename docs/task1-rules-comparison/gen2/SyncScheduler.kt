package com.londongemsapp.data.worker

import androidx.work.WorkManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncScheduler @Inject constructor(
    private val workManager: WorkManager,
) {
    fun schedulePeriodicSync() {
        SyncWorker.enqueuePeriodicSync(workManager)
    }

    fun cancelPeriodicSync() {
        workManager.cancelUniqueWork(SyncWorker.WORK_NAME)
    }
}
