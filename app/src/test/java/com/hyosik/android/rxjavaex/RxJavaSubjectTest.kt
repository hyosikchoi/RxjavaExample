package com.hyosik.android.rxjavaex

import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.Test

class RxJavaSubjectTest {

    @Test
    fun `publish subject 테스트`() {

        val src = PublishSubject.create<String>()

        src.subscribe(Consumer {  })

    }

}