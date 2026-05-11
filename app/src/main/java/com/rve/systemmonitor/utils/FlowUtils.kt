package com.rve.systemmonitor.utils

import android.util.Log
import com.rve.systemmonitor.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion

object FlowUtils {
    fun <T> pollingFlow(tag: String, delayMillis: Long, fetcher: () -> T): Flow<T> = flow {
        if (BuildConfig.DEBUG) Log.d(tag, "$tag Stream Started with delay: $delayMillis")
        while (true) {
            if (BuildConfig.DEBUG) Log.d(tag, "$tag Stream Updated")
            emit(fetcher())
            delay(delayMillis)
        }
    }.onCompletion {
        if (BuildConfig.DEBUG) Log.d(tag, "$tag Stream Stopped")
    }.flowOn(Dispatchers.IO)
}
