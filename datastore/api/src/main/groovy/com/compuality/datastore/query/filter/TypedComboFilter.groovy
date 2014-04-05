package com.compuality.datastore.query.filter

interface TypedComboFilter<T> extends TypedFilter<T> {

  List<Filter> getFilters()

  List<TypedFilter> getTypedFilters()
}
