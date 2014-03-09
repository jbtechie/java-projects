package com.compuality.elasticsearch

import com.google.common.collect.Lists
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.hibernate.validator.constraints.NotEmpty

class ElasticSearchConfiguration {

  @NotEmpty
  String clusterName = 'elasticsearch'

  @NotEmpty
  List<InetSocketTransportAddress> transportAddresses = Lists.asList(new InetSocketTransportAddress('localhost', 9300))
}
