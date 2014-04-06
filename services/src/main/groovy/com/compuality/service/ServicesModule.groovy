package com.compuality.service
import com.compuality.datastore.Datastore
import com.compuality.datastore.elasticsearch.ElasticSearchDatastore
import com.compuality.datastore.elasticsearch.ElasticSearchDatastoreConfig
import com.compuality.elasticsearch.client.ElasticSearchClientProvider
import com.compuality.elasticsearch.client.impl.ClientProvider
import com.compuality.service.datastore.DatastoreService
import com.compuality.service.gutenberg.GutenbergImportService
import com.google.inject.AbstractModule
import com.google.inject.Scopes

class ServicesModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(ElasticSearchClientProvider).to(ClientProvider).in(Scopes.SINGLETON)
    bind(ElasticSearchDatastoreConfig).in(Scopes.SINGLETON)
    bind(Datastore).to(ElasticSearchDatastore).in(Scopes.SINGLETON)

    install(new WebServiceModule(DatastoreService, GutenbergImportService))
  }
}
