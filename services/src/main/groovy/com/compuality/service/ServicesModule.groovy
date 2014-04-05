package com.compuality.service
import com.compuality.datastore.Datastore
import com.compuality.datastore.elasticsearch.ElasticSearchDatastore
import com.compuality.datastore.elasticsearch.ElasticSearchDatastoreConfig
import com.compuality.elasticsearch.client.ElasticSearchClientProvider
import com.compuality.elasticsearch.client.impl.ClientProvider
import com.compuality.inject.WebService
import com.compuality.service.datastore.DatastoreService
import com.google.inject.AbstractModule
import com.google.inject.Scopes
import com.google.inject.multibindings.Multibinder

class ServicesModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(ElasticSearchClientProvider).to(ClientProvider).in(Scopes.SINGLETON)
    bind(ElasticSearchDatastoreConfig).in(Scopes.SINGLETON)
    bind(Datastore).to(ElasticSearchDatastore).in(Scopes.SINGLETON)

    Multibinder<WebService> servicesBinder = Multibinder.newSetBinder(binder(), WebService)
    servicesBinder.addBinding().to(DatastoreService).in(Scopes.SINGLETON)
  }
}
