package com.compuality.datastore.elasticsearch

class ElasticSearchDatastoreConfig {

  String index = 'default'
  int shards = 5
  int replicas = 0
  int maxResultSize = 10000
}
