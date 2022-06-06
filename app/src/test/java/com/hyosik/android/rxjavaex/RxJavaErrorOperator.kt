package com.hyosik.android.rxjavaex

import io.reactivex.rxjava3.core.Observable
import org.junit.Test

class RxJavaErrorOperator {

    @Test
    fun `subscribe 에서 throwable 핸들링`() {
        Observable.just("1","2","a","3")
            .map { i -> i.toInt() }
            .subscribe (
                {println(it)},
                {println("Error!!")}
            )
    }

    @Test
    fun `onErrorReturn 연산자 테스트`() {
        Observable.just("1","2","a","3")
            .map { i -> i.toInt() }
            .onErrorReturn {-1}
            .subscribe { println(it)}
        // 오류가 발생하면 아이템 발행을 종료하고 , onError() 를 호출하는 대신에 오류 처리를 위한 함수를 실행한다.
    }

    @Test
    fun `onErrorResumeNext 연산자 테스트`() {
        Observable.just("1","2","a","3")
            .map { i -> i.toInt() }
            .onErrorResumeNext { Observable.just(100,200,300)}
            .subscribe { println(it)}
        // 오류가 발생시 기존 스트림을 종료시키고 , 다른 Observable 소스로 스트림을 대체한다.
    }

    @Test
    fun `retry 연산자 테스트`() {
        Observable.just("1","2","a","3")
            .map { i -> i.toInt() }
            .retry(5)
            .subscribe(
                { println(it)},
                {error -> error.printStackTrace()}
            )
        // 오류 발생시 재시도를 적힌 횟수 만큼 시도한다.
    }

}