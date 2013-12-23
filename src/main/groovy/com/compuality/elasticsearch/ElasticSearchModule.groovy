package com.compuality.elasticsearch

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.io.Files
import com.google.inject.AbstractModule
import com.google.inject.Provider
import org.elasticsearch.client.Client
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.node.Node

import java.nio.charset.StandardCharsets

import static org.elasticsearch.node.NodeBuilder.nodeBuilder

class ElasticSearchModule extends AbstractModule {

  @Override
  protected void configure() {
    String source = Files.toString(new File("elasticsearch.yml"), StandardCharsets.UTF_8)
    Settings settings = ImmutableSettings.builder().loadFromSource(source).build()
    Node node = nodeBuilder().settings(settings).node()
    bind(Client).toProvider({ node.client() } as Provider)
//    bind(ClientTest).asEagerSingleton()
    bind(ObjectMapper).toInstance(new ObjectMapper())
    bind(ElasticSearchDAO)
    bind(DAOTest).asEagerSingleton()
  }
}
