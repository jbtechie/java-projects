package com.compuality.elasticsearch
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.io.Files
import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import org.elasticsearch.client.Client
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.node.Node

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

import static org.elasticsearch.node.NodeBuilder.nodeBuilder

class ElasticSearchModule extends AbstractModule {

  @Override
  protected void configure() {
//    bind(ClientTest).asEagerSingleton()
    bind(Charset).toInstance(StandardCharsets.UTF_8)
    bind(ObjectMapper).toInstance(new ObjectMapper())
//    bind(ElasticSearchDAO)
//    bind(DAOTest).asEagerSingleton()
//    bind(ElasticSearchBenchmark).asEagerSingleton()
    bind(EdgeMappingBenchmarks).asEagerSingleton()
  }

  @Provides
  @Singleton
  Node getNode(ElasticSearchConfiguration config) {
    String source = Files.toString(new File(config.configFile), StandardCharsets.UTF_8)
    Settings settings = ImmutableSettings.builder().loadFromSource(source).build()
    return nodeBuilder().settings(settings).node()
  }

  @Provides
  Client getClient(Node node) {
    return node.client()
  }
}
