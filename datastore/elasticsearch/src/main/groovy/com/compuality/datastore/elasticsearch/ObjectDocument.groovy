package com.compuality.datastore.elasticsearch

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.As
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id

class ObjectDocument<T> {

  public static final String SUPER_CLASSES = 'super_classes'
  public static final String INTERFACES = 'interfaces'
  public static final String OBJECT = 'object'

  Set<Class<? super T>> superClasses = [] as Set

  Set<Class<?>> interfaces = [] as Set

  @JsonTypeInfo(use=Id.CLASS, include=As.EXTERNAL_PROPERTY, property='objectClass')
  T object

  ObjectDocument() {

  }

  ObjectDocument(T object) {
    this.object = object

    addInterfaces(interfaces, object.class)

    Class<?> superclass = object.class.superclass
    while(superclass && superclass != Object.class) {
      this.superClasses
    }
  }

  private static void addInterfaces(Set<Class<?>> interfaces, Class<?> clazz) {
    if(clazz) {
      if(clazz.interface) {
        interfaces.add(clazz)
      }
      clazz.interfaces.each { addInterfaces(interfaces, it) }
      addInterfaces(interfaces, clazz.superclass)
    }
  }
}
