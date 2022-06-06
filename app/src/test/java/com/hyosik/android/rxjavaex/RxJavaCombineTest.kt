package com.hyosik.android.rxjavaex


import io.reactivex.rxjava3.core.Observable
import org.junit.Test
import java.util.concurrent.TimeUnit

class RxJavaCombineTest {

    @Test
    fun `combineLatest 연산자 테스트 (중요! 실무에서 많이 사용)`() {
        val src1 = Observable.create<Int>{ emitter ->
            Thread{
                for(i in 1 until 5) {
                    emitter.onNext(i)
                    try {
                        Thread.sleep(1000)
                    } catch (e : Exception) {
                        e.printStackTrace()
                    }
                }
            }.start()
        }

        val src2 = Observable.create<String> { emitter ->
            try {
                Thread {
                    Thread.sleep(500)
                    emitter.onNext("A")
                    Thread.sleep(700)
                    emitter.onNext("B")
                    Thread.sleep(100)
                    emitter.onNext("C")
                    Thread.sleep(700)
                    emitter.onNext("D")
                }.start()
            } catch (e : Exception) {
                e.printStackTrace()
            }
        }

        Observable.combineLatest(src1,src2,{num , str -> "${num}$str"})
            .subscribe {println(it)}
        Thread.sleep(5000)

        /** 두 Observable 에서 가장 최근에 발행한 아이템을 취합하여 하나로 발행하는 연산자이다. */
        /** 실무에서 많이 사용되는 연산자 중 하나로 , 여러개의 http 요청에 의한 응답을 하나로 묶어서 처리할 때 사용한다. */
    }

    @Test
    fun `zip 연산자 테스트`() {
        val src1 = Observable.create<Int>{ emitter ->
            Thread{
                for(i in 1 until 5) {
                    emitter.onNext(i)
                    try {
                        Thread.sleep(1000)
                    } catch (e : Exception) {
                        e.printStackTrace()
                    }
                }
            }.start()
        }

        val src2 = Observable.create<String> { emitter ->
            try {
                Thread {
                    Thread.sleep(500)
                    emitter.onNext("A")
                    Thread.sleep(700)
                    emitter.onNext("B")
                    Thread.sleep(100)
                    emitter.onNext("C")
                    Thread.sleep(700)
                    emitter.onNext("D")
                }.start()
            } catch (e : Exception) {
                e.printStackTrace()
            }
        }
        Observable.zip(src1,src2,{num , str -> "${num}$str"})
            .subscribe {println(it)}
        Thread.sleep(5000)

        /** combineLatest하고 비슷하지만 발행되는 결과가 다르다. */
        /** combineLatest는 가장 최근에 발행한 아이템을 기준으로 결합을 하지만 */
        /** zip 은 여러 Observable의 발행 순서를 엄격히 지켜 아이템을 결합한다. */
        /** combineLatest 는 그래서 결과가 1A 2A 2B 2C 이런식으로 진행하지만 */
        /** zip 은 1A 2B 3C 이런식으로 진행한다. */
    }

    @Test
    fun `merge 연산자 테스트`() {
        val src1 = Observable.intervalRange(
            1,
            10,
            0,
            100,
            TimeUnit.MILLISECONDS
        ).map { value -> value * 20 }

        val src2 = Observable.create<Int> { emitter ->
            Thread{
                try {
                    Thread.sleep(350)
                    emitter.onNext(1)
                    Thread.sleep(200)
                    emitter.onNext(1)
                } catch (e : Exception) {
                    e.printStackTrace()
                }
            }.start()
        }
        Observable.merge(src1,src2).subscribe { println(it)}
        Thread.sleep(1000)

        // 여러 Observable 을 그냥 합쳐서 하나의 Observable 로 발행 한다.
    }

}