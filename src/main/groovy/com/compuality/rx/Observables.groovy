package com.compuality.rx

import rx.Observable

class Observables {

  static final Observable complete = Observable.create({ it.onCompleted() })
}
