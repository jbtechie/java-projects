package com.compuality.datastore.query.filter
import com.google.common.collect.Lists

class TypedOrFilter<T> implements TypedComboFilter<T> {

  private final List<Filter> filters
  private final List<TypedFilter<T>> typedFilters

  TypedOrFilter(Filter... filters) {
    this.filters = Lists.newArrayList(filters)
    this.typedFilters = Lists.newArrayList()
  }

  TypedOrFilter(TypedFilter<T>... typedFilters) {
    this.filters = Lists.newArrayList()
    this.typedFilters = Lists.newArrayList(typedFilters)
  }

  List<Filter> getFilters() {
    return filters
  }

  List<TypedFilter<T>> getTypedFilters() {
    return typedFilters
  }

  TypedOrFilter<T> or(Filter... filters) {
    this.filters.addAll(filters)
    return this
  }

  TypedOrFilter<T> or(TypedFilter<T>... filters) {
    this.typedFilters.addAll(filters)
    return this
  }
}
