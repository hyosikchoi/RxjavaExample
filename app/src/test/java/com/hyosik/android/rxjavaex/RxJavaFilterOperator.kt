package com.hyosik.android.rxjavaex

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Consumer
import org.junit.Test
import java.util.concurrent.TimeUnit

class RxJavaFilterOperator {

    @Test
    fun `debounce 연산자 테스트`() {
        Observable.create<Int> { emitter ->
            emitter.onNext(1)
            Thread.sleep(100)
            emitter.onNext(2)
            emitter.onNext(3)
            emitter.onNext(4)
            emitter.onNext(5)
            Thread.sleep(100)
            emitter.onNext(6)
        }.debounce(10 , TimeUnit.MILLISECONDS)
            .subscribe { println(it) }
        Thread.sleep(300)

        // debounce 연산자는 특정 시간 동안 다른 아이템이 발행되지 않을 때만 아이템을 발행하도록 한다.
        // debounce 에 10 밀리세컨드 이상 다른 아이템이 발행되지 않을 때 발행되도록 해놨다.
        // 그러므로 슬립을 100밀리세컨드로 걸어놓은 1,5 와 마지막에 300밀리세컨드로 걸어놓은 6 이 발행된다.
        // 반복적으로 빠르게 발행된 아이템들을 필터링 할 때 유용하다.
    }

    @Test
    fun `distinct 연산자 테스트`() {
        Observable.just(1,2,2,1,3)
            .distinct()
            .subscribe { item -> println(item) }
        // 중복을 필터링하여 제거하는 연산자이다.
    }

    @Test
    fun `elementAt 연산자 테스트`() {
        Observable.just(1,2,3,4)
            .elementAt(2)
            .subscribe { item -> println(item) }
        // 특정 인덱스의 아이템을 발행한다.
    }

    @Test
    fun `filter 연산자 테스트 (중요!)`() {
        Observable.just(2,30,22,5,60,1)
            .filter { x -> x > 10 }
            .subscribe { println(it) }
        // 조건식이 true 일 때 해당 아이템만 발행한다.
    }

    @Test
    fun `sample , skip 연산자 테스트`() {
        Observable.create<Long> { emitter ->
            emitter.onNext(1)
            Thread.sleep(600)
            emitter.onNext(2)
            emitter.onNext(3)
            emitter.onNext(4)
            Thread.sleep(300)
            emitter.onComplete()
        }
            .sample(300 , TimeUnit.MILLISECONDS)
            .subscribe {println(it)}
        Thread.sleep(1000)
        // sample 연산자는 일정 시간 간격으로 아이템을 샘플링했다.

        Observable.just(1,2,3,4)
            .skip(2)
            .subscribe{ println(it)}
        // skip 연산자는 발행하는 아이템을 순차적으로 n개를 무시하고 이후에 나오는 아이템을 발행하는 연산자이다.
    }

    @Test
    fun `take 연산자 테스트`() {
        Observable.just(1,2,3,4)
            .take(2)
            .subscribe { println(it) }
        // skip 연산자와 반대로 순차적으로 n개만 발행하는 연산자이다.
    }

    @Test
    fun `all 연산자 테스트`() {
        Observable.just(2,1)
            .all{x -> x > 0 }
            .subscribe(Consumer{println(it)})
        // 모든 아이템이 특정 조건을 만족하는지 반환을 boolean 으로 한다.
    }

    @Test
    fun `amb 연산자 테스트`() {
        val list = ArrayList<Observable<Int>>()
        list.add(Observable.just(20,40,60).delay(100,TimeUnit.MILLISECONDS))
        list.add(Observable.just(1,2,3))
        list.add(Observable.just(0,0,0).delay(200,TimeUnit.MILLISECONDS))
        Observable.amb(list).subscribe {println(it)}
        // 여러개의 Observable들을 동시에 구독하고, 그중 가장 먼저 아이템을 발행하는 Observable을 선택하게 된다.
        // 즉 위에서는 1,2,3 Observable 이 선택된다.
    }

}