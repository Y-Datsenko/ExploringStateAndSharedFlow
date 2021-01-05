package com.example.explorekotlinflows

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
class SingleShotEventsWithSharedFlowTest {

    @Test
    fun `we will lose emitted values when sharedFlow doesn't have subscribers`() =
        runBlockingTest {
            val sharedFlow = MutableSharedFlow<Int>()
            val receivedValues = mutableListOf<Int>()

            launch {
                sharedFlow.emit(1)
                sharedFlow.emit(2)
            }
            val job = launch { sharedFlow.toList(receivedValues) }

            receivedValues shouldHaveSize 0
            job.cancel()
        }

    @Test
    fun `we will get emitted values when sharedFlow has subscriber`() =
        runBlockingTest {
            val sharedFlow = MutableSharedFlow<Int>()
            val receivedValues = mutableListOf<Int>()

            val job = launch { sharedFlow.toList(receivedValues) }
            launch {
                sharedFlow.emit(1)
                sharedFlow.emit(2)
            }

            receivedValues shouldContainExactly listOf(1, 2)
            job.cancel()
        }

    @Test
    fun `we will get emitted values when we are waiting for subscriber before emitting`() =
        runBlockingTest {
            val sharedFlow = MutableSharedFlow<Int>()
            val receivedValues = mutableListOf<Int>()

            launch {
                delay(2000)
                sharedFlow.emit(1)
                sharedFlow.emit(2)
            }
            val job = launch {
                delay(1000)
                sharedFlow.toList(receivedValues)
            }
            advanceTimeBy(2000)

            receivedValues shouldContainExactly listOf(1, 2)
            job.cancel()
        }

    @Test
    fun `we will lose emitted values when sharedFlow doesn't have subscribers but has extraBufferCapacity`() =
        runBlockingTest {
            val sharedFlow = MutableSharedFlow<Int>(extraBufferCapacity = 2)
            val receivedValues = mutableListOf<Int>()

            launch {
                sharedFlow.emit(1)
                sharedFlow.emit(2)
            }
            val job = launch { sharedFlow.toList(receivedValues) }

            receivedValues shouldHaveSize 0
            job.cancel()
        }
}
