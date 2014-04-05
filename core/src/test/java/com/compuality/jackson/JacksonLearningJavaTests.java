package com.compuality.jackson;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

public class JacksonLearningJavaTests {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  public void testExternalTypeProperty() throws Exception {
    String json = mapper.writeValueAsString(new Container());
    Container container = mapper.readValue(json, Container.class);
  }

  public static class Container {

    private Class<?> embeddedClass;

    @JsonTypeInfo(use= JsonTypeInfo.Id.CLASS, include= JsonTypeInfo.As.EXTERNAL_PROPERTY, property="embeddedClass")
    private Embedded embedded = new Embedded();

    public Class<?> getEmbeddedClass() {
      return embeddedClass;
    }

    public void setEmbeddedClass(Class<?> embeddedClass) {
      this.embeddedClass = embeddedClass;
    }

    public Embedded getEmbedded() {
      return embedded;
    }

    public void setEmbedded(Embedded embedded) {
      this.embedded = embedded;
    }
  }

  public static class Embedded {

    private int val = 3;

    public int getVal() {
      return val;
    }

    public void setVal(int val) {
      this.val = val;
    }
  }
}
