package com.compuality.rx
import com.google.common.collect.Lists
import com.yammer.metrics.Metrics
import com.yammer.metrics.core.Meter
import com.yammer.metrics.core.Timer
import com.yammer.metrics.core.TimerContext
import rx.Observable
import rx.concurrency.Schedulers

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class ObservableTest {

//    @Test
    void testSingleThread() {
//      def a = Observable.range(Integer.MIN_VALUE, Integer.MAX_VALUE)
//          .mapMany({ Observable.from(it) })
//          .subscribe()
//      def b = Observable.range(Integer.MIN_VALUE, Integer.MAX_VALUE)

//      long l = a.map({ it.longValue() })
//          .reduce(0L, { al, bl -> al + bl })
//          .toBlockingObservable()
//          .single()
      Observable.interval(1, TimeUnit.MILLISECONDS).mapMany({ Observable.from(it) }).subscribe()

      Thread.sleep(60*1000)
//          .mapMany({ Observable.from(it) })

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

//  @Test
//  void testMultiThreadSerialMapMany() {
//    List<Observable<Integer>> processors = processors()
//
//    Meter meter = Metrics.newMeter(getClass(), 'testMultiThreadSerialMapMany processor', 'items processed', TimeUnit.SECONDS)
//    Timer timer = Metrics.newTimer(getClass(), 'testMultiThreadSerialMapMany process time', TimeUnit.SECONDS, TimeUnit.SECONDS)
//    TimerContext time = timer.time()
//
//    Observables.parallelMerge(Observable.from(processors))
//        .doOnEach({ meter.mark() })
//        .doOnError({ println "error: ${it}"})
//        .count()
//        .doOnEach({ time.stop(); println "testMultiThreadSerialMapMany   processed ${it} items in ${timer.mean()} s at an average of ${meter.meanRate()} items/s"})
//        .subscribe()
//  }

//  @Test
  void testMultiThreadIterableMapMany() {
    List<Observable<Integer>> processors = processors()

    Meter meter = Metrics.newMeter(getClass(), 'testMultiThreadIterableMapMany processor', 'items processed', TimeUnit.SECONDS)
    Timer timer = Metrics.newTimer(getClass(), 'testMultiThreadIterableMapMany process time', TimeUnit.SECONDS, TimeUnit.SECONDS)
    TimerContext time = timer.time()

    Observables.parallelMerge(Observable.from(processors))
        .doOnEach({ meter.mark() })
        .doOnError({ println "error: ${it}"})
        .count()
        .doOnEach({ time.stop(); println "testMultiThreadIterableMapMany processed ${it} items in ${timer.mean()} s at an average of ${meter.meanRate()} items/s"})
        .subscribe()
  }

//  @Test
  void testSingleThreadTheirs() {
    Iterable<Observable<Integer>> processors = processors()

    Meter meter = Metrics.newMeter(getClass(), 'testSingleThreadTheirs processor', 'items processed', TimeUnit.SECONDS)
    Timer timer = Metrics.newTimer(getClass(), 'testSingleThreadTheirs process time', TimeUnit.SECONDS, TimeUnit.SECONDS)
    TimerContext time = timer.time()

    Observable.merge(Observable.from(processors))
        .doOnEach({ meter.mark() })
        .doOnError({ println "error: ${it}"})
        .count()
        .doOnEach({ time.stop(); println "testSingleThreadTheirs         processed ${it} items in ${timer.mean()} s at an average of ${meter.meanRate()} items/s"})
        .subscribe()
  }

//  @Test
  void testMultiThreadTheirs() {
    Iterable<Observable<Integer>> processors = processors()

    Meter meter = Metrics.newMeter(getClass(), 'testMultiThreadTheirs processor', 'items processed', TimeUnit.SECONDS)
    Timer timer = Metrics.newTimer(getClass(), 'testMultiThreadTheirs process time', TimeUnit.SECONDS, TimeUnit.SECONDS)
    TimerContext time = timer.time()

    Observable<?> o = Observable.from(processors).parallel({
          println 'parallelizing'
          return it.mapMany({ println 'mapping'; it })
        }, Schedulers.executor(Executors.newFixedThreadPool(4)))
        .doOnEach({ meter.mark() })
        .doOnError({ println "error: ${it}"; it.printStackTrace() })
        .count()
//        .reduce(0, { a, b -> a + b })
        .doOnEach({ time.stop(); println "testMultiThreadTheirs          processed ${it} items in ${timer.mean()} s at an average of ${meter.meanRate()} items/s"})

//    o.subscribe()
    subscribeAndWait(o)

//    Observable.parallelMerge(Observable.from(processors), 1, Schedulers.threadPoolForIO())
//        .doOnEach({
//          it.doOnEach({ meter.mark() })
//              .doOnError({ println "error: ${it}"})
//              .count()
//              .doOnEach({ time.stop(); println "testMultiThreadTheirs          processed ${it} items in ${timer.mean()} s at an average of ${meter.meanRate()} items/s"})
//              .subscribe()
//        })
//        .subscribe()

//    Thread.sleep(3000)
  }

  static void subscribeAndWait(Observable<?> observable) {
    AtomicBoolean done = new AtomicBoolean()
    observable.finallyDo({
      done.set(true)
      synchronized(done) {
        done.notify()
      }
    })
    .subscribe()

    while(!done.get()) {
      try {
        synchronized(done) {
          done.wait()
        }
      } catch(InterruptedException e) {
        // nothing to do
      }
    }
  }

  private static Iterable<Observable<Integer>> processors() {
    int numThreads = 8
    int min = 0
    int max = 1e6 * 60 * 10 / numThreads

    List<Observable<Integer>> processors = Lists.newArrayList()
    (1..numThreads).each {
      def p = Observable.range(min, max).mapMany({ Observable.from(it) })//.map({ Thread.sleep(1); it })
//      p = Observable.create(new IterableMapMany(p, { ImmutableList.of(it) }))
      processors.add(p)
    }

    return processors
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
