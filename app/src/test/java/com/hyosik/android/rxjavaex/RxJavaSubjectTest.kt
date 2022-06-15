package com.hyosik.android.rxjavaex

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.subjects.*
import org.junit.Test
import java.lang.Thread.sleep
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class RxJavaSubjectTest {

    @Test
    fun `publish subject 테스트`() {

        val src = PublishSubject.create<String>()

        src.subscribe(
            {item -> println("A: $item")},
            {t -> t.printStackTrace()},
            {println("A: onComplete")}
        )
        src.subscribe(
            {item -> println("B: $item")},
            {t -> t.printStackTrace()},
            {println("B: onComplete")}
        )
        src.onNext("Hello")
        src.onNext("World")
        src.onNext("!!!")
        src.onComplete()
        // Subject는 Hot Observable 이라는 사실을 잊으면 안된다.
    }

    @Test
    fun `publish subject merge 처리 테스트`() {

        val src1 = Observable.interval(1,TimeUnit.SECONDS)
        val src2 = Observable.interval(500,TimeUnit.MILLISECONDS)

        val subject = PublishSubject.create<String>()
        src1.map { item -> "A: $item" }.subscribe(subject)
        src2.map { item -> "B: $item" }.subscribe(subject)
        subject.subscribe { item -> println(item)}
        Thread.sleep(5000)
        // Subject를 통해 아이템을 재발행도하고, merge 연산자처럼 src1,src2 두 Observable 소스를 묶어서
        // 이벤트를 subject 하나로 관리하는것도 가능하다.
    }

    @Test
    fun `serializedSubject 설명 및 테스트`() {
        val counter = AtomicInteger()
        val subject = PublishSubject.create<Any>().toSerialized()
        subject.doOnNext{i -> counter.incrementAndGet()}
            .doOnNext { i  -> counter.decrementAndGet()}
            .filter { i  -> counter.get() != 0}
            .subscribe(
                { i -> println(i)},
                {t -> t.printStackTrace()}
            )
        val runnable = Runnable {
            for(i in 0 until 100000) {
                try {
                    Thread.sleep(1)
                } catch (t : Throwable) {
                    t.printStackTrace()
                }
                subject.onNext(i)
            }
        }
        Thread(runnable).start()
        Thread(runnable).start()
        Thread.sleep(1000)
        println("종료")
        // 두 개의 스레드가 동시에 메모리에 접근하면 스레드가 안전하지 않은 경우가 생길 수 있다.
        // 그러므로 그럴땐 toSerialized() 메서드를 통해 SerializedSubject를 객체로 생성할 수 있다.
        // SerializedSubject는 내부에서 synchronized 키워드를 통해 스레드를 제어해 스레드에 안전한 Subject를 제공한다.

    }

    @Test
    fun `BehaviorSubject 테스트`() {
        val subject = BehaviorSubject.create<Int>()
        subject.subscribe{ item -> println("A: $item")}
        subject.onNext(1)
        subject.onNext(2)
        subject.subscribe{item -> println("B: $item")}
        subject.onNext(3)
        subject.subscribe{item -> println("C: $item")}

        // 새로운 구독 시 시작할 때 가장 마지막 아이템만을 발행하게 한다.
        // 그 이후에는 PublishSubject와 동일하게 모두 수신할 수 있다.
    }

    @Test
    fun `ReplaySubject 테스트`() {

        val subject = ReplaySubject.create<Int>()
        subject.subscribe{item -> println("A: $item")}
        subject.onNext(1)
        subject.onNext(2)
        subject.subscribe{item -> println("B: $item")}
        subject.onNext(3)
        subject.subscribe{item -> println("C: $item")}

        // 새로운 구독자가 구독 시 이전에 발행했던 아이템 모두를 구독자에게 전달한다.
        // PublishSubject 에 cache 연산자를 적용한 것과 유사하다.
        /** 큰 볼륨이나 무한한 아이템을 발행하는 소스에 대해서는 사용성에 대해 고민 해봐야한다. */
        /** 안그러면 OOM 이 뜰것이다. */
    }

    @Test
    fun `AsyncSubject 테스트`() {

        val subject = AsyncSubject.create<Int>()
        subject.subscribe{item -> println("A: $item")}
        subject.onNext(1)
        subject.onNext(2)
        subject.subscribe{item -> println("B: $item")}
        subject.onNext(3)
        subject.onComplete()
        subject.subscribe{item -> println("C: $item")}

        // onComplete() 가 호출되기 직전 발행된 아이템만을 전달 하는 특징이 있다.
        // 위에선 A,B,C, 전부다 3 만 출력된다.
    }

    @Test
    fun `UnicastSubject 테스트`() {

        val subject = UnicastSubject.create<Any>()
        Observable.interval(1, TimeUnit.SECONDS)
            .subscribe(subject)
        sleep(3000)
        subject.subscribe{i -> println("A : $i")}
        sleep(2000)

        // Observer가 subject를 구독하기 전까지는 발행하는 아이템을 버퍼에 저장하고 , 구독 시 버퍼에 있던 아이템을 모두 발행하고 버퍼를 비워낸다.
        // 그러므로 구독자를 여러개 둘 수 없다. 첫 번째 구독자가 모든 버퍼에 있던 아이템을 소비하면 두 번째 구독자부터는 아이템을 수신할 수 없기 때문이다.
        // 위에서는 3초동안 버퍼에 쌓인 아이템을 3초 이후 모두 방출하고 그 이후 2초동안은 1초마다 발행한 아이템을 출력한다.
    }

}