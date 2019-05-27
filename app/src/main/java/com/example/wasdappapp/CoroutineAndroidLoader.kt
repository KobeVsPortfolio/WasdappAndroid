package com.example.wasdappapp

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.android.Main

inline fun <reified T> T.loge(error: Throwable, message: () -> String) = Log.d(T::class.simpleName, message(), error)

internal class CoroutineLifecycleListener(private val deferred: Deferred<*>) : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun cancelCoroutine() {
        if (!deferred.isCancelled) {
            deferred.cancel()
        }
    }
}

internal val Background = newFixedThreadPoolContext(Runtime.getRuntime().availableProcessors() * 2, "Loader")

fun <T> LifecycleOwner.load(loader: suspend () -> T): Deferred<T> {
    val deferred = GlobalScope.async(context = Background, start = CoroutineStart.LAZY, block = {
        loader()
    })

    lifecycle.addObserver(CoroutineLifecycleListener(deferred))
    return deferred
}

infix fun <T> Deferred<T>.then(block: suspend (T) -> Unit): Job {
    return GlobalScope.launch(context = Dispatchers.Main, block = {
        try {
            block(this@then.await())
        } catch (e: Exception) {
            loge(e) { "Exception in then()!" }
            throw e
        }
    })
}