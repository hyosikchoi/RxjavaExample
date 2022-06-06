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

    @Test
    fun `map 연산자 테스트(중요!)`() {
        val intSrc = Observable.create<Int> { emitter ->
            emitter.onNext(1)
            emitter.onNext(2)
            emitter.onNext(3)
            emitter.onComplete()
        }
        val strSrc = intSrc.map { value -> value * 10 }
        strSrc.subscribe { println(it) }
        // map 은 발행되는 값에 대해 원하는 수식이나 다른타입으로 변환시킬 수 있다.
    }

    @Test
    fun `flatMap 연산자 테스트(중요!)`() {
        Observable.range(2,8)
            .flatMap { x -> Observable.range(1,9)
                .map { y -> String.format("${x}*${y} = ${x*y}")}
            }.subscribe {println(it)}
        // flatMap 연산자는 Observable 을 또 다른 Observable 로 변환시킨다. 위에선 x 를 range 1~9의 또다른 Observable 로 변환시켰다.
        // 그러고나서 각 변환된 x를 다시 병합하고 나서 방출 시킨다. 그러므로 1:N 형태로 새로운 시퀀스가 발행된다.
        // 1:N 이라는건 위에선 각 x 마다 1~9까지 9개의 새로운 시퀀스를 발행한다.
    }

    @Test
    fun `buffer 연산자 테스트`() {
        Observable.range(0,10)
            .buffer(3)
            .subscribe { i ->
                println("버퍼 데이터 발행")
                for(j in i) {
                    println("#${j}")
                }
            }
        // buffer 연산자는 Observable이 발행하는 아이템을 묶어서 List로 발행한다.
        /** 에러를 발행하는 경우 이미 발행된 아이템들이 버퍼에 포함되더라도 버퍼를 발행하지 않고 에러를 즉시 발행한다. */
    }

    @Test
    fun `scan 연산자 테스트`() {
        Observable.range(1,5)
            .scan { t1, t2 ->
                print(String.format("${t1}+${t2}="))
                return@scan t1+t2
            }.subscribe { println(it) }
        // scan 연산자는 최소 두개의 아이템이 있어야 하며 , 처음에는 t1이 그대로 발행된다.
        // 피보나치 처럼 t1,t2 의 연산결과가 다음 t1의 인자가 된다.
        // 이렇게 계속 누적이 되기 때문에 누산기 라고도 한다.
    }

    @Test
    fun `groupBy 연산자 테스트(중요!)`() {
        Observable.just(
            "Magenta Circle",
            "Cyan Circle",
            "Yellow Triangle",
            "Yellow Circle",
            "Magenta Triangle",
            "Cyan Triangle",
        ).groupBy { item ->
            if(item.contains("Circle")) return@groupBy "C"
            else if (item.contains("Triangle")) return@groupBy "T"
            else return@groupBy "None"
        }.subscribe { group ->
            println("${group.key} 발행 시작")
            group.subscribe { shape -> println("${group.key} : $shape") }
        }
        // 원을 C로 분류하고 , 삼각형은 T로 분류한 다음 각 GroupedObservable을 병렬로 발행한다.
    }

}