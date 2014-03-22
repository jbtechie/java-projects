package com.compuality.rx

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import rx.Observable

class Observables {

  static <T> Observable<T> parallelMerge(Observable<Observable<T>> sequences) {
    return Observable.create(new ParallelMerge<>(sequences, Executors.newCachedThreadPool()))
  }

  static <T> Observable<T> parallelMerge(Observable<Observable<T>> sequences, ExecutorService executor) {
    return Observable.create(new ParallelMerge<>(sequences, executor))
  }

  private Observables() {}
}
