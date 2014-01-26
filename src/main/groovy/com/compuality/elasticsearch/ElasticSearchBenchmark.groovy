package com.compuality.elasticsearch

import org.elasticsearch.action.bulk.BulkRequestBuilder
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.client.Client
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Inject

public class ElasticSearchBenchmark {

  private static final Logger log = LoggerFactory.getLogger(ElasticSearchBenchmark)

  @Inject
  public ElasticSearchBenchmark(Client client) {

    println randomAlphaString(10)

    long totalLength = 0
    double totalTime = 0

    (1..10).each {
      BulkRequestBuilder bulkBuilder = client.prepareBulk()
      (1..1e5).each {
        bulkBuilder.add(client.prepareIndex('experiment', 'benchmark')
            .setSource(/{ "value": "${randomAlphaString(10)}" }/.toString())
            .setId(RANDOM_ID_FUNC().toString()))
      }

      BulkResponse bulkResponse = bulkBuilder.execute().actionGet()

      totalLength += bulkResponse.items.length
      totalTime += bulkResponse.tookInMillis / 1000.0

      if(bulkResponse.hasFailures()) {
        throw new RuntimeException('Bulk load had errors')
      } else {
        log.debug(/Partial load of ${String.format('%.1e', (double)bulkResponse.items.length)} documents in ${bulkResponse.tookInMillis/1000.0} s at ${(double)bulkResponse.items.length/(bulkResponse.tookInMillis/1000.0)} per second s/)
      }
    }

    log.debug(/Total load of ${String.format('%.1e', (double)totalLength)} documents in ${totalTime} s at ${totalLength/totalTime} per second s/)
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
