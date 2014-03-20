package com.compuality.rx;

import rx.Observable;
import rx.util.functions.Func1;

public class MemoryTest {

    public static void main(String[] args) {
      Observable.range(Integer.MIN_VALUE, Integer.MAX_VALUE)
          .mapMany(new Func1<Integer, Observable<Integer>>() {
              @Override
              public Observable<Integer> call(Integer integer) {
                  return Observable.from(integer);
              }
          })
          .subscribe();
    }
}
