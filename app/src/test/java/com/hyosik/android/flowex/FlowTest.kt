package com.hyosik.android.flowex

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

class FlowTest {

    @Test
    fun `flow emit 테스트`() = runTest {

        flow<Int> {
            emit(1)
            emit(2)
            emit(3)
            emit(4)
        }.map { i -> i * 2 }
            .collect { value : Int -> println(value) }
    }

    @Test
    fun `flow flatMapMerge 테스트`() = runTest {
        val printIntFlow = flow<Int>{
            for (i in 0 until 5) {
                emit(i)
            }
        }

        printIntFlow.flatMapMerge { intvalue ->
            flow {
                emit(intvalue)
                delay(1000)
                emit(intvalue + 0.5)
            }
        }.collect {
            println(it)
        }

    }

}