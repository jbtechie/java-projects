package com.compuality.datastore.query.filter

import com.google.common.collect.Lists

import static com.google.common.base.Preconditions.checkNotNull

class FieldValuesFilter implements Filter {

  private final String fieldPath
  private final List<Object> values

  FieldValuesFilter(String fieldPath, Object... values) {
    this.fieldPath = checkNotNull(fieldPath)
    this.values = Lists.newArrayList(values)
  }

  String getFieldPath() {
    return fieldPath
  }

  List<Object> getValues() {
    return values
  }
}
