package com.compuality.elasticsearch
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import com.google.inject.Provider
import org.elasticsearch.action.bulk.BulkItemResponse
import org.elasticsearch.action.bulk.BulkRequestBuilder
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.client.Client
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Subscription
import rx.observables.ConnectableObservable
import rx.util.functions.Func1

class ElasticSearchDAO {

  private static Logger logger = LoggerFactory.getLogger(ElasticSearchDAO)

  private Provider<Client> clientProvider
  private ObjectMapper mapper

  @Inject
  ElasticSearchDAO(Provider<Client> clientProvider, ObjectMapper mapper) {
    this.clientProvider = clientProvider
    this.mapper = mapper
  }

  Observable<BulkItemResponse> addDocuments(String index, String type, Observable<String> documents) {
    return bulkIndex(index, type, RANDOM_ID_FUNC, documents).subscribe()
  }

  Observable<BulkItemResponse> addObjects(String index, String type, Observable<Object> objects) {
    return bulkIndex(index, type, RANDOM_ID_FUNC, objects.map({ mapper.writeValueAsString(it) }))
  }

  Observable<BulkItemResponse> addDocumentsWithHashId(String index, String type, Observable<String> documents) {
    return bulkIndex(index, type, HASH_ID_FUNC, documents)
  }

  Observable<BulkItemResponse> addObjectsWithHashId(String index, String type, Observable<Object> objects) {
    return bulkIndex(index, type, HASH_ID_FUNC, objects.map({ mapper.writeValueAsString(it) }))
  }

  Observable addObjectsWithHashIdAndTime(String index, String type, Observable<Object> objects) {
    ConnectableObservable<Object> connectable = objects.publish()

    Observable original = connectable.map({ it })
    original.subscribe()

//    connectable.timeInterval().subscribe({ logger.debug("Bulk time interval: " + it.getIntervalInMilliseconds()) })

    Observable<BulkItemResponse> responses = bulkIndex(index, type, HASH_ID_FUNC, connectable.map({ mapper.writeValueAsString(it) }))
    responses.subscribe()

    Observable zipped = Observable.zip(original, responses, { o, r -> [o, r] })

    return Observable.create({ observer ->
      Subscription s = zipped.subscribe(observer)
      connectable.connect()
      return s
    })
  }

  private Observable<BulkItemResponse> bulkIndex(String index, String type, Func1<String, UUID> idFunc, Observable<String> documents) {
    Client client = clientProvider.get()
    return documents.buffer(1000)
      .mapMany({ documentBuffer ->
        BulkRequestBuilder bulkBuilder = client.prepareBulk()
        documentBuffer.each {
          bulkBuilder.add(client.prepareIndex(index, type)
                                .setSource(it)
                                .setId(idFunc.call(it).toString()))
        }
        BulkResponse bulkResponse = bulkBuilder.execute().actionGet()
        return Observable.from(bulkResponse.getItems())
      })
      .doOnError({ logger.error("Error performing bulk index.") })
      .finallyDo({ client.close() })
      .finallyDo({
        logger.debug("Done performing bulk index.")
      })
  }

  private static final RANDOM_ID_FUNC = { UUID.randomUUID() }
  private static final HASH_ID_FUNC = { UUID.nameUUIDFromBytes(it.bytes) }
}
