package com.compuality.elasticsearch.store.document

import com.google.common.base.Joiner
import com.google.common.base.Splitter
import org.elasticsearch.client.Client

class StoreUtil {

  private static final Joiner INDEX_JOINER = Joiner.on('.')
  private static final Splitter INDEX_SPLITTER = Splitter.on('.')

  private final Client client

  public static String appendVersion(String index, int version) {
    return INDEX_JOINER.join(index, version)
  }

  public static int parseVersion(String index) {
    return INDEX_SPLITTER.splitToList(index).last() as int
  }
}
