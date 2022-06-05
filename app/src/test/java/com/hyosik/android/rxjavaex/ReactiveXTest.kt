package com.hyosik.android.rxjavaex

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.Test
import java.lang.Exception
import java.util.concurrent.TimeUnit

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

    @Test
    fun `Single testing`() {

        Single.just("Hello World")
            .subscribe { item ->  println(item) }

        Single.create<String> { emitter -> emitter.onSuccess("Create") }
            .subscribe { item -> println(item) }

    }

    @Test
    fun `Disposable testing`() {
        val source = Observable.interval(1000 , TimeUnit.MILLISECONDS)
//        val source = Observable.just("A","B" , "C")

        //1초에 한 번씩 아이템 발행
        val disposable = source.subscribe{println(it)}

        Thread {
            try {
                Thread.sleep(3500)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            disposable.dispose()
        }.start()
    }



}