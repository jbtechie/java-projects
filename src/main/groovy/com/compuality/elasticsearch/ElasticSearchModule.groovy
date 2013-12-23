package com.compuality.elasticsearch
import com.google.inject.AbstractModule
import com.google.inject.Provider
import org.elasticsearch.client.Client
import org.elasticsearch.node.Node

import static org.elasticsearch.node.NodeBuilder.nodeBuilder

class ElasticSearchModule extends AbstractModule {

  @Override
  protected void configure() {
    Node node = nodeBuilder().local(true).node()
    bind(Client).toProvider({ node.client() } as Provider)
    bind(ClientTest).asEagerSingleton()
  }
}
