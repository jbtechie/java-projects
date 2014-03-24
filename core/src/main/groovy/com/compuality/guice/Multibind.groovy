package com.compuality.guice

import java.lang.annotation.*

@Target([ElementType.METHOD, ElementType.FIELD])
@Retention(RetentionPolicy.RUNTIME)
public @interface Multibind {

  /**
   * If null, binds to property or method return type.
   */
  Class<?> bindClass() default void.class

  Class<? extends Annotation> annotatedWith() default Annotation.class
}