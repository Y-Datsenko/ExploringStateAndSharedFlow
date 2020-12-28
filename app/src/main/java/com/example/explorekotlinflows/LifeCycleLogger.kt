package com.example.explorekotlinflows

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

class LifeCycleLogger(
    private val tag: String
) : LifecycleEventObserver {

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        Log.d(tag, "onStateChanged $event state ${source.lifecycle.currentState}")
    }
}
