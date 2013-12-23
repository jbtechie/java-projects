package com.compuality.elasticsearch

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Throwables
import com.google.inject.Inject
import org.elasticsearch.action.bulk.BulkRequestBuilder
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.client.Client
import rx.Observable
import rx.Observer
import rx.util.functions.Func1

import javax.inject.Provider

class ElasticSearchDAO {

  private Provider<Client> clientProvider
  private ObjectMapper mapper

  @Inject
  ElasticSearchDAO(Provider<Client> clientProvider, ObjectMapper mapper) {
    this.clientProvider = clientProvider
    this.mapper = mapper
  }

  void addDocuments(String index, String type, Observable<String> documents) {
    documents.subscribe(new BulkLoadingObserver(clientProvider.get(), index, type))
  }

  void addObjects(String index, String type, Observable<Object> objects) {
    addDocuments(index, type, objects.map({ mapper.writeValueAsString(it) } as Func1))
  }

  private static class BulkLoadingObserver implements Observer<String> {

    private Client client
    private String index
    private String type
    private BulkRequestBuilder builder

    BulkLoadingObserver(Client client, String index, String type) {
      this.client = client
      this.index = index
      this.type = type
      this.builder = client.prepareBulk()
    }

    @Override
    void onNext(String doc) {
      println "adding ${doc}"
      builder.add(client.prepareIndex(index, type).setSource(doc))
    }

    @Override
    void onCompleted() {
      println 'executing'
      BulkResponse response = builder.execute().actionGet()
      if(response.hasFailures()) {
        println 'FAILURES'
        response.each {
          println "\t${it.failureMessage}"
        }
      }
    }

    @Override
    void onError(Throwable e) {
      Throwables.propagate(e)
    }
  }
}
