package com.compuality.elasticsearch.query
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import org.junit.Test

import java.lang.reflect.Proxy

import static org.hamcrest.core.IsEqual.equalTo
import static org.junit.Assert.assertThat

class ElasticSearchPathProxyHandlerTests {

  interface Event {

    User getUser()

    interface User {
      String getName()
    }
  }

  @Test
  void test() {
    ElasticSearchPathProxyHandler handler = new ElasticSearchPathProxyHandler(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES)

    Event proxy = Proxy.newProxyInstance(Event.classLoader, [Event.class] as Class<?>[], handler) as Event
    proxy.user.name

    assertThat(handler.getPath(), equalTo("user.name"))
  }
}
