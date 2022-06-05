package com.hyosik.android.rxjavaex

import org.junit.Test
import java.util.*
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit

class RxJavaOperator {

    @Test
    fun `defer 연산자 테스트`() {
        val justSrc = Observable.just( System.currentTimeMillis())
        val deferSrc = Observable.defer { Observable.just(System.currentTimeMillis()) }

        try {
            Thread.sleep(5000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        justSrc.subscribe { println(it) } // 1654446031870
        deferSrc.subscribe { println(it)} // 1654446036947

        // 일반 just는 생성즉시 시간을 가지고 있다가 구독시 발행을 한다.
        // defer는 구독시에 생성을 하면서 발행을 하므로 5초의 시간 뒤가 나온다.

    }

    @Test
    fun `empty , never 연산자 테스트`() {
        Observable.empty<String>()
            .doOnTerminate {println("empty 종료")}
            .subscribe()
        Observable.never<String>()
            .doOnTerminate {println("never 종료")  }
            .subscribe()
        // empty 연산자는 아이템을 발행하지는 않지만, 정상적으로 스트림을 종료시킨다.
        // never 연산자는 아이템을 발행하지 않지만 , 정상적으로 종료시키지도 않는다.
    }

    @Test
    fun `interval 연산자 테스트`() {
        val disposable = Observable.interval(1 , TimeUnit.SECONDS)
            .subscribe { println(it) }
        Thread.sleep(5000)
        disposable.dispose()
        // interval 연산자는 일정시간 간격대로 발행을 한다.
    }

}