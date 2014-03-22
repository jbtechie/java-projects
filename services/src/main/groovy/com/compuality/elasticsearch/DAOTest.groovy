package com.compuality.elasticsearch

import com.google.inject.Inject
import com.yammer.metrics.Metrics
import com.yammer.metrics.core.MetricName
import com.yammer.metrics.core.TimerContext
import groovy.json.JsonBuilder
import org.elasticsearch.client.Client
import org.elasticsearch.client.Requests
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rx.Observable

import java.util.concurrent.TimeUnit

import static com.compuality.elasticsearch.ElasticSearchBenchmark.randomAlphaString

class DAOTest {

  private static Logger logger = LoggerFactory.getLogger(DAOTest)

  @Inject
  DAOTest(ElasticSearchDAO dao, Client client) {
//    dao.addObjectsWithHashId('experiments', 'exp',
//      Observable.<Map>from([ [label:1], [label:2], [label:4]]))
//        .subscribe({ logger.debug("add response: " + it.toString()) })

    client.admin().indices().delete(Requests.deleteIndexRequest("experiments")).actionGet()

    int count = (int)1e6

    Timer timer = Metrics.newTimer(DAOTest, 'load test', TimeUnit.SECONDS, TimeUnit.SECONDS)
    TimerContext time = timer.time()
    dao.addDocuments('experiments', 'speedTest', Observable.range(0, count).map{ /{ "value":"${randomAlphaString(10)}" }/.toString() })
        .finallyDo({
          time.stop()
          logger.debug(/${String.format('%.1e', (double)count)} documents loaded in ${timer.mean()} s at ${count/timer.mean()} per s/)
          logger.debug(new JsonBuilder(Metrics.defaultRegistry().allMetrics().get(new MetricName(ElasticSearchDAO, 'index'))).toPrettyString())
        })
        .subscribe()
  }
}
