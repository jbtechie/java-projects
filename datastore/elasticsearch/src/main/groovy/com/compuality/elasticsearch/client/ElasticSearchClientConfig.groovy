package com.compuality.elasticsearch.client

import com.google.common.io.Resources

class ElasticSearchClientConfig {

  private static final URL DEFAULT_RESOURCE_CONFIG = Resources.getResource(ElasticSearchClientConfig, 'elasticsearch.yml')

  URI nativeConfig = DEFAULT_RESOURCE_CONFIG.toURI()

  ElasticSearchClientConfig() {
  }

  ElasticSearchClientConfig(String nativeConfig) {
    this.nativeConfig = URI.create(nativeConfig)
  }

  ElasticSearchClientConfig(URI nativeConfig) {
    this.nativeConfig = nativeConfig
  }

  ElasticSearchClientConfig(URL nativeConfig) {
    this.nativeConfig = nativeConfig.toURI()
  }
}
