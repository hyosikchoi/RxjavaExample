package com.hyosik.android.rxjavaex

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.Test
import java.util.concurrent.TimeUnit

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

}