package com.example.explorekotlinflows.first

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class FirstFragmentViewModel : ViewModel() {

    private val logTag = this::class.java.simpleName
    private val _events = MutableSharedFlow<FirstFragmentEvents>()
    val events: Flow<FirstFragmentEvents>
        get() = _events

    init {
        Log.d(logTag, "init")
        sendEvent(FirstFragmentEvents.MessageFromInit)
    }

    fun onNextClicked() {
        sendEvent(FirstFragmentEvents.GoToNextScreen)
    }

    private fun sendEvent(event: FirstFragmentEvents) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }

    sealed class FirstFragmentEvents {
        object GoToNextScreen : FirstFragmentEvents()
        object MessageFromInit : FirstFragmentEvents()
    }
}