package com.compuality.elasticsearch
import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
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
  }

  @Provides
  @Singleton
  Client getClient(ElasticSearchConfiguration config) {

    Builder settingsBuilder = ImmutableSettings.builder().loadFromClasspath(resourceConfig)

    if(config.elasticsearchConfig) {
      config.elasticsearchConfig.withInputStream { configStream ->
        settingsBuilder.loadFromStream(config.elasticsearchConfig.name, configStream)
      }
    }

    nodeBuilder().settings(settingsBuilder.build()).node().client()
  }
}
