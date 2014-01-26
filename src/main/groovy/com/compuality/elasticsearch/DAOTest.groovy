package com.compuality.elasticsearch

import com.google.inject.Inject
import com.yammer.metrics.Metrics
import com.yammer.metrics.core.TimerContext
import com.yammer.metrics.core.Timer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rx.Observable

import java.util.concurrent.TimeUnit

class DAOTest {

  private static Logger logger = LoggerFactory.getLogger(DAOTest)

  @Inject
  DAOTest(ElasticSearchDAO dao) {
//    dao.addObjectsWithHashId('experiments', 'exp',
//      Observable.<Map>from([ [label:1], [label:2], [label:4]]))
//        .subscribe({ logger.debug("add response: " + it.toString()) })

    Random rand = new Random()
    Timer timer = Metrics.newTimer(DAOTest, 'load test', TimeUnit.SECONDS, TimeUnit.SECONDS)
    TimerContext time = timer.time()
    dao.addDocuments('experiments', 'speedTest', Observable.range(0, 10000000).map{ /{ "value":${rand.nextInt()} }/.toString() })
        .finallyDo({
          time.stop()
          println 'Documents per second: ' + (10000000/timer.mean())
        })
        .subscribe()
  }
}
