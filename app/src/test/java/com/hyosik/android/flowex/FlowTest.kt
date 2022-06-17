package com.hyosik.android.flowex

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
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

}