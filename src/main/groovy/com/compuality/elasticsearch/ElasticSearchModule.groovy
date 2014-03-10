package com.compuality.elasticsearch

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.google.common.base.Splitter
import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import com.yammer.dropwizard.json.ObjectMapperFactory
import org.elasticsearch.client.Client
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.common.transport.TransportAddress

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class ElasticSearchModule extends AbstractModule {

  private static final Splitter TRANSPORT_SPLITTER = Splitter.on(':').limit(2)

  @Override
  protected void configure() {
//    bind(ClientTest).asEagerSingleton()
    bind(Charset).toInstance(StandardCharsets.UTF_8)
//    bind(ElasticSearchDAO)
//    bind(DAOTest).asEagerSingleton()
//    bind(ElasticSearchBenchmark).asEagerSingleton()
    bind(EdgeMappingBenchmarkTask).asEagerSingleton()
  }

  @Provides
  @Singleton
  Client getClient(ElasticSearchConfiguration config) {
    Settings settings = ImmutableSettings.settingsBuilder()
        .put("cluster.name", config.clusterName)
        .build()

    TransportAddress[] addresses = config.transportAddresses.collect { String addr ->
      Iterator<String> parts = TRANSPORT_SPLITTER.split(addr).iterator()
      String host = parts.next()
      int port = parts.next() as int
      return new InetSocketTransportAddress(host, port)
    }.toArray() as TransportAddress[]

    return new TransportClient(settings).addTransportAddresses(addresses)
  }

  @Provides
  @Singleton
  ObjectMapperFactory getMapperFactory() {
    ObjectMapperFactory factory = new ObjectMapperFactory()
    factory.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    factory.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES)
    return factory
  }

  @Provides
  ObjectMapper getMapper(ObjectMapperFactory factory) {
    return factory.build()
  }
}
