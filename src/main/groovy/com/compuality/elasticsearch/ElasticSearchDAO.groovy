package com.compuality.elasticsearch
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Throwables
import com.google.inject.Inject
import com.google.inject.Provider
import org.elasticsearch.action.bulk.BulkRequestBuilder
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.action.index.IndexRequestBuilder
import org.elasticsearch.client.Client
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Observer
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
    documents.map({ client.prepareIndex(index, type).setSource(it) } as Func1)
        .subscribe(new BulkLoadingObserver(client, index, type))
  }

  void addObjects(String index, String type, Observable<Object> objects) {
    addDocuments(index, type, objects.map({ mapper.writeValueAsString(it) } as Func1))
  }

  void addDocumentsWithHashId(String index, String type, Observable<String> documents) {
    Client client = clientProvider.get()
    documents.map({ client.prepareIndex(index, type).setSource(it).setId(UUID.nameUUIDFromBytes(it.bytes).toString()) } as Func1)
      .subscribe(new BulkLoadingObserver(client, index, type))
  }

  void addObjectsWithHashId(String index, String type, Observable<Object> objects) {
    addDocumentsWithHashId(index, type, objects.map({ mapper.writeValueAsString(it) } as Func1))
  }

//  private static class DocumentToIndexRequestBuilderWithHash implements Func1<String, IndexRequestBuilder> {
//    @Override
//    IndexRequestBuilder call(String o) {
//      return null
//    }
//  }

  private static class BulkLoadingObserver implements Observer<IndexRequestBuilder> {

    private Client client
    private String index
    private String type
    private BulkRequestBuilder builder

    BulkLoadingObserver(Client client, String index, String type) {
      logger.debug("created")
      this.client = client
      this.index = index
      this.type = type
      this.builder = client.prepareBulk()
    }

    @Override
    synchronized void onNext(IndexRequestBuilder indexRequestBuilder) {
      logger.debug("adding {}", indexRequestBuilder)
      builder.add(indexRequestBuilder)
    }

    @Override
    void onCompleted() {
      logger.debug('executing')
      BulkResponse response = builder.execute().actionGet()
      if(response.hasFailures()) {
        logger.error('FAILURES')
        response.each {
          logger.error("\t${it.failureMessage}")
        }
      }
    }

    @Override
    void onError(Throwable e) {
      Throwables.propagate(e)
    }
  }
}
