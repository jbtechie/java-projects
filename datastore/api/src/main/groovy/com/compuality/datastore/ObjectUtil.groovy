package com.compuality.datastore

import com.google.common.base.Joiner

class ObjectUtil {

  private static final Joiner FIELD_PATH_JOINER = Joiner.on('.')

  static Joiner fieldPathJoiner() {
    return FIELD_PATH_JOINER
  }
}
