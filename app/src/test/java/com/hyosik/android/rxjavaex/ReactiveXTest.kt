package com.hyosik.android.rxjavaex

import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.Test

class ReactiveXTest {

    @Test
    fun `imperative programing`() {
        val items = ArrayList<Int>()
        items.add(1)
        items.add(2)
        items.add(3)
        items.add(4)

        for(i in items) {
            if( i % 2 == 0) {
                println(i)
            }
        }
    }

    @Test
    fun `reactive programing`() {
        val items = PublishSubject.create<Int>()
        items.onNext(1)
        items.onNext(2)
        items.onNext(3)
        items.onNext(4)

        items.filter { item -> item % 2 == 0 }
            .subscribe { item -> println(item) }

        items.onNext(5)
        items.onNext(6)
        items.onNext(7)
        items.onNext(8)

    }

}