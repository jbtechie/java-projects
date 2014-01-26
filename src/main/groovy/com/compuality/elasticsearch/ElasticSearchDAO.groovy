package com.compuality.elasticsearch

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import com.google.inject.Provider
import com.yammer.metrics.Metrics
import com.yammer.metrics.core.Timer
import com.yammer.metrics.core.TimerContext
import org.elasticsearch.action.bulk.BulkItemResponse
import org.elasticsearch.action.bulk.BulkRequestBuilder
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.client.Client
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rx.Observable
import rx.util.functions.Func1

class ElasticSearchDAO {

  private static Logger logger = LoggerFactory.getLogger(ElasticSearchDAO)

  private final Provider<Client> clientProvider
  private final ObjectMapper mapper
  private final Timer indexTimer = Metrics.newTimer(ElasticSearchDAO, 'index')

  @Inject
  ElasticSearchDAO(Provider<Client> clientProvider, ObjectMapper mapper, ElasticSearchConfiguration config) {
    this.clientProvider = clientProvider
    this.mapper = mapper
  }

  Observable<BulkResponse> addDocuments(String index, String type, Observable<String> documents) {
    return bulkIndex(index, type, RANDOM_ID_FUNC, documents)
  }

  Observable<BulkItemResponse> addObjects(String index, String type, Observable<Object> objects) {
    return bulkIndex(index, type, RANDOM_ID_FUNC, objects.map({ mapper.writeValueAsString(it) })).map({ it.bulkItemResponse })
  }

  Observable<BulkItemResponse> addDocumentsWithHashId(String index, String type, Observable<String> documents) {
    return bulkIndex(index, type, HASH_ID_FUNC, documents).map({ it.bulkItemResponse })
  }

  Observable<BulkItemResponse> addObjectsWithHashId(String index, String type, Observable<Object> objects) {
    return bulkIndex(index, type, HASH_ID_FUNC, objects.map({ mapper.writeValueAsString(it) })).map({ it.bulkItemResponse })
  }

  private static class BulkIndexResult {
    String document
    BulkItemResponse bulkItemResponse
  }

  private Observable<BulkIndexResult> bulkIndex(String index, String type, Func1<String, UUID> idFunc, Observable<String> documents) {
    Client client = clientProvider.get()
    return documents.buffer(1000)
      .map({ documentBuffer ->
        BulkRequestBuilder bulkBuilder = client.prepareBulk()
        documentBuffer.each {
          bulkBuilder.add(client.prepareIndex(index, type)
                                .setSource(it)
                                .setId(idFunc.call(it).toString()))
        }
        TimerContext time = indexTimer.time()
        BulkResponse bulkResponse = bulkBuilder.execute().actionGet()
        time.stop()
        return bulkResponse
      })
      .doOnError({ logger.error("Error performing bulk index.", it) })
      .finallyDo({ client.close() })
      .finallyDo({ logger.debug("Done performing bulk index.") })
  }

  private static final RANDOM_ID_FUNC = { UUID.randomUUID() }
  private static final HASH_ID_FUNC = { UUID.nameUUIDFromBytes(it.bytes) }
}
