package com.compuality.rx

import org.junit.Test
import rx.Observable

class ObservableTest {

//    @Test
    void testSingleThread() {
      def a = Observable.range(Integer.MIN_VALUE, Integer.MAX_VALUE)
      def b = Observable.range(Integer.MIN_VALUE, Integer.MAX_VALUE)

      long l = Observable.merge(a, b).map({ it.longValue() })
          .reduce(0L, { al, bl -> al + bl })
          .toBlockingObservable()
          .single()
    }

//  @Test
  void testMultiThread() {
    def a = Observable.range(Integer.MIN_VALUE, Integer.MAX_VALUE).mapMany({ Observable.from(it) })
    def b = Observable.range(Integer.MIN_VALUE, Integer.MAX_VALUE)

    long l = com.compuality.rx.Observables.parallelMerge(Observable.from(a, b)).map({ it.longValue() })
        .reduce(0L, { al, bl -> al + bl })
        .toBlockingObservable()
        .single()
  }

  @Test
  void testMultiThreadSerialMapMany() {
    def a = Observable.range(Integer.MIN_VALUE, Integer.MAX_VALUE)
    a = Observable.create(new SerialMapMany(a, { Observable.from(it) }))
    def b = Observable.range(Integer.MIN_VALUE, Integer.MAX_VALUE)

    long l = com.compuality.rx.Observables.parallelMerge(Observable.from(a, b)).map({ it.longValue() })
        .reduce(0L, { al, bl -> al + bl })
        .toBlockingObservable()
        .single()
  }

//  @Test
  void testAnonClasses() {
    Interface<String> f = new Interface<String>() {
      @Override
      String method() {
        return 'method'
      }
    }

    println f.method()
  }

  static interface Interface<T> {
    T method()
  }
}
