package com.compuality.jackson

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.As
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Test

class JacksonLearningTests {

  private final ObjectMapper mapper = new ObjectMapper()

  @Test
  public void testExternalTypeProperty() throws Exception {
    String json = mapper.writeValueAsString(new Container(embedded:new Embedded()))
    println(json)
    Container container = mapper.readValue(json, Container.class)
  }

  static class Container {


    @JsonTypeInfo(use=Id.CLASS, include=As.EXTERNAL_PROPERTY, property='embeddedClass')
    Embedded embedded
  }

  static class Embedded {

    int val = 3
  }
}
