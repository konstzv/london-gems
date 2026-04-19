package com.londongems.sync

import android.content.Context
import androidx.startup.Initializer
import androidx.work.WorkManager

class SyncInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        SyncWorker.enqueue(WorkManager.getInstance(context))
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(androidx.work.impl.WorkManagerInitializer::class.java)
    }
}
