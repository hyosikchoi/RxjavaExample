package com.hyosik.android.rxjavaex

import io.reactivex.rxjava3.core.Observable
import org.junit.Test
import java.lang.IllegalArgumentException
import java.util.concurrent.TimeUnit

class RxJavaDoonOperator {

    @Test
    fun `doOnEach 연산자 테스트`() {
        Observable.just(1,2,3)
            .doOnEach { notification ->
                val i = notification.value
                val isOnNext = notification.isOnNext
                val isOnComplete = notification.isOnComplete
                val isOnError = notification.isOnError
                val throwable = notification.error
                println("i : $i")
                println("isOnNext : $isOnNext")
                println("isOnComplete : $isOnComplete")
                println("isOnError : $isOnError")
                if(throwable != null) {
                    println("throwable : $throwable")
                }

            }.subscribe { println(it) }
        // Observable 이 아이템을 발행하기 전에 이를 콜백으로 확인할 수 있도록 해준다.
        // 콜백은 Notification 형태로 들어온다.
    }

    @Test
    fun `doOnNext 연산자 테스트`() {
        Observable.just(1,2,3)
            .doOnNext { item ->
                if(item > 1) {
                    throw IllegalArgumentException()
                }
            }.subscribe(
                {println(it)},
                {error -> error.printStackTrace()}
            )
        // doOnEach 와 비슷하지만 Notification 대신 간단히 발행된 아이템을 확인할 수 있는 Consumer를 파라미터로 넘긴다.
    }

    @Test
    fun `doOnSubscribe 연산자 테스트`() {
        Observable.just(1,2,3)
            .doOnSubscribe { disposable -> println("구독 시작!!") }
            .subscribe { println(it)}
        // 구독시마다 콜백을 받을 수 있도록 한다.
        // 매개변수로 Disposable을 받을 수 있다.
    }

    @Test
    fun `doOnComplete,doOnError 연산자 테스트`() {
        Observable.just(1,2,3)
            .doOnComplete { println("완료") }
            .subscribe {
                println(it)
            }
        // Emitter의 onComplete() 호출로 Observable이 정상적으로 종료 될 때 호출 되는 콜백함수다.

        Observable.just(2,1,0)
            .map{ i ->10/i}
            .doOnError { throwable -> println("오류!!")}
            .subscribe(
                {println(it)},
                {t -> t.printStackTrace()}
            )
        // Observable 내부에서 onError() 호출로 Observable 이 정상적으로 종료되지 않을 때 호출되는 콜백함수다.
    }

    @Test
    fun `doOnTerminate,doOnDispose 연산자 테스트`() {
        Observable.just(2,1,0)
            .map { i -> 10/i }
            .doOnComplete { println("doOnComplete") }
            .doOnTerminate { println("doOnTerminate")}
            .subscribe(
                { println(it)},
                {t -> t.printStackTrace()}
            )
        // onComplete 연산자와 비슷하게 Observable 이 종료 될 때 호출 되지만
        /** 오류가 발생했을 때도 콜백이 호출 된다는 차이점이 있다. */

        val src = Observable.interval(500 , TimeUnit.MILLISECONDS)
            .doOnDispose { println("doOnDispose") }

        val dispose = src.subscribe { println(it) }
        Thread.sleep(1100)
        dispose.dispose()
        // 구독 중인 스트림이 dispose() 메서드 호출로 인해 폐기 되는 경우 호출되는 콜백함수다.
    }

    @Test
    fun `doFinally 연산자 테스트`() {
        val src1 = Observable.intervalRange(1,2,0,500,TimeUnit.MILLISECONDS)
            .doOnComplete{println("doOnComplete")}
            .doOnTerminate{println("doOnTerminate")}
            .doFinally { println("doFinally") }
        val src2 = Observable.interval(500 , TimeUnit.MILLISECONDS)
            .doOnComplete{println("doOnComplete")}
            .doOnTerminate{println("doOnTerminate")}
            .doFinally { println("doFinally") }
        val disposable1 = src1.subscribe {println(it)}
        val disposable2 = src2.subscribe { println(it)}
        Thread.sleep(1100)
        disposable1.dispose()
        disposable2.dispose()

        /** doOnComplete,doOnTerminate는 중간에 dispose 시킨게 아닌 Observable이 정상 종료 될 때 호출이 된다.(에러로 인한 종료 포함) */
        // doFinally는 정상종료 말고도 스트림이 폐기 될 때 호출이 된다.
        // Observable을 구독한 뒤 종료되는 어떠한 상황에서도 후속 조치를 해야 하는 상황에서 사용할 수 있다.
    }


}