package com.compuality.elasticsearch

import com.compuality.guice.WebService
import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Scopes
import com.google.inject.Singleton
import com.google.inject.multibindings.Multibinder
import org.elasticsearch.client.Client
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.common.settings.ImmutableSettings.Builder

import static org.elasticsearch.node.NodeBuilder.nodeBuilder

class ElasticSearchModule extends AbstractModule {

  private static final String DEFAULT_RESOURCE_CONFIG = 'com/compuality/elasticsearch/default.yml'

  private final String resourceConfig

  ElasticSearchModule() {
    this(DEFAULT_RESOURCE_CONFIG)
  }

  ElasticSearchModule(String resourceConfig) {
    this.resourceConfig = resourceConfig
  }

  @Override
  protected void configure() {
    Multibinder<WebService> resourceBinder = Multibinder.newSetBinder(binder(), WebService)
    resourceBinder.addBinding().to(ElasticSearchBenchmarkResource).in(Scopes.SINGLETON)
  }

  @Provides
  @Singleton
  Client client(ElasticSearchConfig config) {

    Builder settingsBuilder = ImmutableSettings.builder().loadFromClasspath(resourceConfig)

    if(config.nativeConfig) {
      config.nativeConfig.withInputStream { configStream ->
        settingsBuilder.loadFromStream(config.nativeConfig.name, configStream)
      }
    }

    nodeBuilder().settings(settingsBuilder.build()).node().client()
  }
}
