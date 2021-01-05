package com.example.explorekotlinflows

import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
class EmitLazilyTest {

    @Test
    fun `we will get emitted values when shared flow gets subscribers`() =
        runBlockingTest {
            val sharedFlow = MutableSharedFlow<Int>()
            val receivedValues = mutableListOf<Int>()

            launch { sharedFlow.emitLazily(1) }
            launch { sharedFlow.emitLazily(2) }
            val job = launch { sharedFlow.toList(receivedValues) }

            receivedValues shouldContainExactly listOf(1, 2)

            job.cancel()
        }

    @Test
    fun `we will get emitted values when shared flow gets subscribers2`() =
        runBlockingTest {
            val sharedFlow = MutableSharedFlow<Int>()
            val publicFlow = sharedFlow.shareIn(this, SharingStarted.Eagerly)
            val receivedValues = mutableListOf<Int>()

            launch { sharedFlow.emit(1) }
            launch { sharedFlow.emit(2) }
            val job = launch { publicFlow.toList(receivedValues) }

            receivedValues shouldContainExactly listOf(1, 2)

            job.cancel()
        }

    @Test
    fun `we will get emitted values when shared flow(with extraBufferCapacity) gets subscribers`() =
        runBlockingTest {
            val sharedFlow = MutableSharedFlow<Int>(extraBufferCapacity = 2)
            val receivedValues = mutableListOf<Int>()

            launch { sharedFlow.emitLazily(1) }
            launch { sharedFlow.emitLazily(2) }
            launch { sharedFlow.emitLazily(3) }
            launch { sharedFlow.emitLazily(4) }
            launch { sharedFlow.emitLazily(5) }
            val job = launch { sharedFlow.toList(receivedValues) }

            receivedValues shouldContainExactly listOf(1, 2, 3, 4, 5)
            job.cancel()
        }

    @Test
    fun `we will get emitted values when shared flow(with extraBufferCapacity and onBufferOverflow) gets subscribers`() =
        runBlockingTest {
            val sharedFlow = MutableSharedFlow<Int>(
                extraBufferCapacity = 2,
                onBufferOverflow = BufferOverflow.DROP_OLDEST
            )
            val receivedValues = mutableListOf<Int>()

            // all five will be suspended. BufferCapacity and Overflow will be applied once first subscriber becomes available
            launch {
                sharedFlow.emitLazily(1)
                println("emitted 1")
            }
            launch {
                sharedFlow.emitLazily(2)
                println("emitted 2")
            }
            launch {
                sharedFlow.emitLazily(3)
                println("emitted 3")
            }
            launch {
                sharedFlow.emitLazily(4)
                println("emitted 4")
            }
            launch {
                sharedFlow.emitLazily(5)
                println("emitted 5")
            }
            val job = launch {
                delay(3000)
                println("before collect")
                sharedFlow.toList(receivedValues)
            }
            advanceTimeBy(3000)

            receivedValues shouldContainExactly listOf(4, 5)
            job.cancel()
        }
}
