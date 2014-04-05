package com.compuality.datastore.elasticsearch

import com.compuality.datastore.query.filter.AndFilter
import com.compuality.datastore.query.filter.ClassFilter
import com.compuality.datastore.query.filter.ComboFilter
import com.compuality.datastore.query.filter.FieldValuesFilter
import com.compuality.datastore.query.filter.Filter
import com.compuality.datastore.query.filter.InterfaceFilter
import com.compuality.datastore.query.filter.OrFilter
import com.compuality.datastore.query.filter.TypedAndFilter
import com.compuality.datastore.query.filter.TypedComboFilter
import com.compuality.datastore.query.filter.TypedFilter
import com.compuality.datastore.query.filter.TypedOrFilter
import org.elasticsearch.index.query.FilterBuilder
import org.elasticsearch.index.query.FilterBuilders

import static com.compuality.datastore.ObjectUtil.fieldPathJoiner

class FilterToFilterBuilder {

  static FilterBuilder fromFilter(Filter filter) {
    switch(filter) {
      case ComboFilter:
        ComboFilter comboFilter = filter as ComboFilter

        List<FilterBuilder> filterBuilders = comboFilter.filters.collect { fromFilter(filter) }

        switch(filter) {
          case AndFilter:
            return FilterBuilders.andFilter(filterBuilders.toArray() as FilterBuilder[])

          case OrFilter:
            return FilterBuilders.orFilter(filterBuilders.toArray() as FilterBuilder[])

          default:
            throw new IllegalArgumentException("ComboFilter '${filter.class.name}' not supported.")
        }

      case FieldValuesFilter:
        FieldValuesFilter fieldValuesFilter = filter as FieldValuesFilter
        String path = fieldPathJoiner().join(ObjectDocument.OBJECT, fieldValuesFilter.fieldPath)
        return FilterBuilders.termsFilter(path, fieldValuesFilter.values)

      default:
        throw new IllegalArgumentException("Filter '${filter.class.name}' not supported.")
    }
  }

  static <T> FilterBuilder fromTypedFilter(TypedFilter<T> filter) {
    switch(filter) {
      case TypedComboFilter:
        TypedComboFilter<T> typedComboFilter = filter as TypedComboFilter<T>

        List<FilterBuilder> filterBuilders = typedComboFilter.filters.collect { fromFilter(it) }
        filterBuilders.addAll(typedComboFilter.typedFilters.collect { fromTypedFilter(it) })

        switch(filter) {
          case TypedAndFilter:
            return FilterBuilders.andFilter(filterBuilders.toArray() as FilterBuilder[])

          case TypedOrFilter:
            return FilterBuilders.orFilter(filterBuilders.toArray() as FilterBuilder[])

          default:
            throw new IllegalArgumentException("TypedComboFilter '${filter.class.name}' not supported.")
        }

      case ClassFilter:
        ClassFilter<T> classFilter = filter as ClassFilter<T>
        return FilterBuilders.typeFilter(classFilter.objectClass.name)

      case InterfaceFilter:
        InterfaceFilter<T> interfaceFilter = filter as InterfaceFilter<T>
        return FilterBuilders.termFilter(ObjectDocument.INTERFACES, interfaceFilter.interface)

      default:
        throw new IllegalArgumentException("TypedFilter '${filter.class.name}' not supported.")
    }
  }
}
