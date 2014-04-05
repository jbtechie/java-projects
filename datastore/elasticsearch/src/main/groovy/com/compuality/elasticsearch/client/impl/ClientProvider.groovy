package com.compuality.elasticsearch.client.impl
import com.compuality.elasticsearch.client.ElasticSearchClientConfig
import com.compuality.elasticsearch.client.ElasticSearchClientProvider
import com.compuality.inject.SingletonValueProvider
import org.elasticsearch.client.Client
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.common.settings.Settings

import javax.inject.Inject

import static org.elasticsearch.node.NodeBuilder.nodeBuilder

class ClientProvider extends SingletonValueProvider<Client> implements ElasticSearchClientProvider {

  private final ElasticSearchClientConfig config

  @Inject
  ClientProvider(ElasticSearchClientConfig config) {
    this.config = config
  }

  @Override
  protected Client create() {
    Settings settings = ImmutableSettings.builder()
        .loadFromClasspath(config.nativeConfigResource)
        .build()

    return nodeBuilder().settings(settings).node().client()
  }
}
