package com.compuality.sandbox.main
import com.compuality.elasticsearch.client.ElasticSearchClientConfig
import com.compuality.inject.Bind
import com.google.common.io.Resources
import com.yammer.dropwizard.config.Configuration

class MainDropwizardAppConfig extends Configuration {

  @Bind
  ElasticSearchClientConfig elasticsearch = new ElasticSearchClientConfig(Resources.getResource(MainDropwizardAppConfig, 'elasticsearch.yml'))
}
