package com.compuality.rx

import rx.Observable
import rx.Observer
import rx.Subscription
import rx.util.functions.Func1

class SerialMapMany<T, R> implements Observable.OnSubscribeFunc<R> {

  private final Observable<T> sequence
  private final Func1<? super T, ? extends Observable<? extends R>> func

  SerialMapMany(Observable<T> sequence, Func1<? super T, ? extends Observable<? extends R>> func) {
    this.sequence = sequence
    this.func = func
  }

  @Override
  Subscription onSubscribe(Observer<? super R> observer) {
    return sequence.subscribe(new SequenceObserver<T>(observer))
  }

  private class SequenceObserver implements Observer<T> {

    private final Observer<? super R> observer

    SequenceObserver(Observer<? super R> observer) {
      this.observer = observer
    }

    @Override
    void onCompleted() {
      observer.onCompleted()
    }

    @Override
    void onError(Throwable e) {
      observer.onError(e)
    }

    @Override
    void onNext(T value) {
      func.call(value).subscribe(new FunctionObserver())
    }

    private class FunctionObserver implements Observer<R> {

      @Override
      void onCompleted() {
        // TODO
      }

      @Override
      void onError(Throwable e) {
        // TODO
      }

      @Override
      void onNext(R output) {
        observer.onNext(output)
      }
    }
  }
}
