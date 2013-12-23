package com.compuality.elasticsearch
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import com.google.inject.Provider
import org.elasticsearch.action.bulk.BulkRequestBuilder
import org.elasticsearch.action.index.IndexRequestBuilder
import org.elasticsearch.client.Client
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rx.Observable
import rx.util.functions.Action0
import rx.util.functions.Action1
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

  void addDocuments(String index, String type, Observable<String> documents) {
    Client client = clientProvider.get()

    Observable<IndexRequestBuilder> requests = documents.map({ client.prepareIndex(index, type)
                                                                     .setSource(it) } as Func1)
    bulkIndex(client, requests)
  }

  void addObjects(String index, String type, Observable<Object> objects) {
    addDocuments(index, type, objects.map({ mapper.writeValueAsString(it) } as Func1))
  }

  void addDocumentsWithHashId(String index, String type, Observable<String> documents) {
    Client client = clientProvider.get()

    Observable<IndexRequestBuilder> requests = documents.map({ client.prepareIndex(index, type)
                                                                     .setSource(it)
                                                                     .setId(UUID.nameUUIDFromBytes(it.bytes).toString()) } as Func1)
    bulkIndex(client, requests)
  }

  void addObjectsWithHashId(String index, String type, Observable<Object> objects) {
    addDocumentsWithHashId(index, type, objects.map({ mapper.writeValueAsString(it) } as Func1))
  }

  private static void bulkIndex(Client client, Observable<IndexRequestBuilder> requests) {
    BulkRequestBuilder bulkRequestBuilder = client.prepareBulk()

    requests.synchronize()
            .doOnEach({ bulkRequestBuilder.add(it) } as Action1)
            .count()
            .doOnEach({ logger.debug('Added {} index requests.', it)} as Action1)
            .doOnCompleted({ bulkRequestBuilder.execute() } as Action0)
            .finallyDo({ client.close() } as Action0)
            .subscribe()
  }
}
