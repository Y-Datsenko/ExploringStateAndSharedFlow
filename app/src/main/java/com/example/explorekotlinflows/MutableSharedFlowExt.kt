package com.example.explorekotlinflows

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first

/**
 * This function emits value when the receiver shared flow gets the first subscriber
 * It helps to avoid loosing events while view is not subscribed to shared flow
 */
suspend fun <T> MutableSharedFlow<T>.emitLazily(value: T) {
    subscriptionCount.first { it > 0 }
    emit(value)
}
