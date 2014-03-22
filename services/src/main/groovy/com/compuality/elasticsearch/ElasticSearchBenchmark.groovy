package com.compuality.elasticsearch

import com.google.common.util.concurrent.AtomicDouble
import com.yammer.metrics.Metrics
import com.yammer.metrics.core.TimerContext
import org.elasticsearch.action.bulk.BulkRequestBuilder
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.client.Client
import org.elasticsearch.client.Requests
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Inject
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

public class ElasticSearchBenchmark {

  private static final Logger log = LoggerFactory.getLogger(ElasticSearchBenchmark)

  @Inject
  public ElasticSearchBenchmark(Client client) {

    client.admin().indices().delete(Requests.deleteIndexRequest("experiments")).actionGet()

    ExecutorService executor = Executors.newFixedThreadPool(8)

    AtomicDouble serialTime = new AtomicDouble()
    AtomicLong count = new AtomicLong()

    Timer timer = Metrics.newTimer(ElasticSearchBenchmark, 'totalLoadTime', TimeUnit.SECONDS, TimeUnit.SECONDS)
    TimerContext time = timer.time()

    List<Callable> callables = (1..100).collect {
      return {
        BulkRequestBuilder bulkBuilder = client.prepareBulk()
        (1..1e4).each {
          bulkBuilder.add(client.prepareIndex('experiments', 'speedTest')
              .setSource(/{ "value": "${randomAlphaString(100)}", "outer":{ "inner" : 3 } }/.toString())
              .setId(RANDOM_ID_FUNC().toString()))
        }

        BulkResponse bulkResponse = bulkBuilder.execute().actionGet()

        serialTime.addAndGet(bulkResponse.tookInMillis/1e3)
        count.addAndGet(bulkResponse.items.length)

        if(bulkResponse.hasFailures()) {
          throw new RuntimeException('Bulk load had errors')
        } else {
          log.debug(/Partial load of ${String.format('%.1e', (double)bulkResponse.items.length)} documents in ${bulkResponse.tookInMillis/1000.0} s at ${(double)bulkResponse.items.length/(bulkResponse.tookInMillis/1000.0)} per second s/)
        }
      }
    }

    executor.invokeAll(callables)

    executor.shutdown()
    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS)

//    callables.each {
//      it.call()
//    }

    time.stop()

    log.debug(/Total serial load of ${String.format('%.1e', (double)count.get())} documents in ${serialTime.get()} s at ${count.get()/serialTime.get()} per s/)
    log.debug(/Wall clock time for load was ${timer.mean()} s at ${count.get()/timer.mean()} per s/)
  }

  private static final RANDOM_ID_FUNC = { UUID.randomUUID() }
  private static final HASH_ID_FUNC = { UUID.nameUUIDFromBytes(it.bytes) }

  private static final String[] ALPHAS = ('a'..'z').toArray()
  private static final Random rand = new Random()

  public static String randomAlphaString(int numChars) {
    StringBuilder builder = new StringBuilder()
    (1..numChars).each {
      builder.append(ALPHAS[rand.nextInt(ALPHAS.length)])
    }
    return builder.toString()
  }
}
