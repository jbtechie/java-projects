package com.compuality.datastore.query.filter
import com.google.common.collect.Lists

class TypedAndFilter<T> implements TypedComboFilter<T> {

  private final List<Filter> filters
  private final List<TypedFilter<T>> typedFilters

  TypedAndFilter(Filter... filters) {
    this.filters = Lists.newArrayList(filters)
    this.typedFilters = Lists.newArrayList()
  }

  TypedAndFilter(TypedFilter<T>... typedFilters) {
    this.filters = Lists.newArrayList()
    this.typedFilters = Lists.newArrayList(typedFilters)
  }

  List<Filter> getFilters() {
    return filters
  }

  List<TypedFilter<T>> getTypedFilters() {
    return typedFilters
  }

  TypedAndFilter<T> and(TypedFilter<T>... filters) {
    this.typedFilters.addAll(filters)
    return this
  }

  TypedAndFilter<T> and(Filter... filters) {
    this.filters.addAll(filters)
    return this
  }
}
