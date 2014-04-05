package com.compuality.elasticsearch.client

import com.compuality.elasticsearch.client.impl.ClientProvider
import com.google.inject.AbstractModule
import com.google.inject.Scopes

import static com.google.common.base.Preconditions.checkNotNull

class ElasticSearchClientModule extends AbstractModule {

  private final ElasticSearchClientConfig config

  ElasticSearchClientModule() {
    this(new ElasticSearchClientConfig())
  }

  ElasticSearchClientModule(ElasticSearchClientConfig config) {
    this.config = checkNotNull(config)
  }

  @Override
  protected void configure() {
    bind(ElasticSearchClientConfig).in(Scopes.SINGLETON)
    bind(ElasticSearchClientProvider).to(ClientProvider).in(Scopes.SINGLETON)
  }
}
