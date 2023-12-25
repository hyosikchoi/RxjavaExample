package com.hyosik.android.rxjavaex

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.delay
import org.junit.Test

class RxJavaScheduler {

    @Test
    fun `subscribeOn 연산자 테스트`() {
        val src = Observable.create<Int> { emitter ->
            for(i in 0 until 3) {
                val threadName = Thread.currentThread().name
                println("#Subs On $threadName : $i")
                emitter.onNext(i)
                Thread.sleep(100)
            }
        }

        src.subscribe { s ->
            val threadName = Thread.currentThread().name
            println("#Obsv On $threadName : $s")
        }

        // io scheduler 사용
        src.subscribeOn(Schedulers.io())
            .subscribe { s ->
                val threadName = Thread.currentThread().name
                println("#Obsv On $threadName : $s")
            }
        Thread.sleep(500)
        // Observable 체인에 subscribeOn 연산자만 있고 observeOn 이 없다면 해당 스케줄러는
        // 아이템 발행 및 구독까지 Observable 체인 전체에 작용한다.
    }

    @Test
    fun `subscribeOn 과 observeOn 연산자 테스트`() {
        Observable.just(1,2,3,4)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .subscribe {println(it)}
            Thread.sleep(200)
        // 아이템 구독은 io Scheduler 로 아이템 발행은 computation Scheduler 로 진행한다.
        // io Scheduler 는 Coroutine 에서 IO Dispatcher 와 같다고 보면 된다.
        // computation Scheduler 는 Coroutine 에서 Default Dispatcher 와 같다고 보면 된다. (계산 적인 작업에 사용)

        /** 실제 작업에선 네트워크 작업으로 구독은 io로 발행(observeOn)은 UI갱신은 main Thread 에서 수행하게끔 한다. */
    }

    @Test
    fun `subscribeOn 과 observeOn 연산자 테스트2`() {
        /** onNext 로 발행을 하기 전까지는 io thread 에서 동작을 하고 */
        /** 발행을 시작하면 computation thread 로 컨텍스트 스위칭을 한다. */
        val os: Observable<Int> = Observable.create { emitter ->
            (0..2).forEach {i ->
                val threadName = Thread.currentThread().name
                println("#Subs on $threadName : $i")
                emitter.onNext(i)
                Thread.sleep(100)
            }
            emitter.onComplete()
        }

        os.observeOn(Schedulers.computation())
            .subscribeOn(Schedulers.io())
            .subscribe { i ->
                val threadName = Thread.currentThread().name
                println("#Obs on $threadName : $i")
            }
        // RXJava 는 비동기 데이터 스트림이므로 Thread sleep 을 걸어줘야 비동기로 그동안에 데이터를 출력함.
        Thread.sleep(500)
    }

}