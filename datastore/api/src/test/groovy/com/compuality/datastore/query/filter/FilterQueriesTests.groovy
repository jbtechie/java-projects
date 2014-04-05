package com.compuality.datastore.query.filter

import org.junit.Test

import static Filters.hasFieldValues
import static Filters.isClass
import static Filters.typedAnd

class FilterQueriesTests {

  @Test
  public void testFilterQueries() throws Exception {
    TypedFilter<Integer> query = isClass(Integer)
    TypedAndFilter<Integer> andQuery = typedAnd(query)
    andQuery.and(hasFieldValues("intValue", 3))
  }
}
