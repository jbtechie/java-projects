package com.compuality.rx
import rx.Observable
import rx.Observer
import rx.Subscription

import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit

import static com.google.common.base.Preconditions.checkNotNull

class ParallelMerge<T> implements Observable.OnSubscribeFunc<T> {

  private final Observable<Observable<T>> sequences
  private final ExecutorService executor

  ParallelMerge(Observable<Observable<T>> sequences, ExecutorService executor) {
    this.sequences = checkNotNull(sequences)
    this.executor = checkNotNull(executor)
  }

  @Override
  Subscription onSubscribe(Observer<? super T> observer) {
    sequences.subscribe(new SequenceObserver<>(sequences, executor, observer))
  }

  /**
   * Can't create an anonymous class because of bug in Groovy.
   */
  private static class SequenceObserver<T> implements Observer<Observable<T>> {

    private final Observable<Observable<T>> sequences
    private final ExecutorService executor
    private final Observer<? super T> observer

    SequenceObserver(Observable<Observable<T>> sequences, ExecutorService executor, Observer<? super T> observer) {
      this.sequences = checkNotNull(sequences)
      this.executor = checkNotNull(executor)
      this.observer = checkNotNull(observer)
    }

    @Override
    void onCompleted() {
      executor.shutdown()
      executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS)
      observer.onCompleted()
    }

    @Override
    void onError(Throwable e) {
      executor.shutdown()
      executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS)
      observer.onError(e)
    }

    @Override
    void onNext(Observable<T> sequence) {
      executor.execute({
        sequence.subscribe({ T t ->
          observer.onNext(t)
        })
      })
    }
  }
}

