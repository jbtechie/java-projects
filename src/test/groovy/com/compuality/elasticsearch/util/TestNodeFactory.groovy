package com.compuality.elasticsearch.util

import com.compuality.elasticsearch.util.constants.GatewayType
import com.compuality.elasticsearch.util.constants.IndexStoreType
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.node.Node

import static org.elasticsearch.common.settings.ImmutableSettings.settingsBuilder
import static org.elasticsearch.node.NodeBuilder.nodeBuilder

class TestNodeFactory {

  private static final Settings SETTINGS = settingsBuilder()
        .put(IndexStoreType.KEY, IndexStoreType.MEMORY)
        .put(GatewayType.KEY, GatewayType.NONE)
        .build()

  static Node create() {
    return nodeBuilder()
        .settings(SETTINGS)
        .local(true)
        .clusterName('test')
        .node()
  }
}
