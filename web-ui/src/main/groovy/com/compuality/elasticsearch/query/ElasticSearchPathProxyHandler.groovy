package com.compuality.elasticsearch.query

import com.compuality.query.PathGenerator
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.google.common.base.Joiner

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

class ElasticSearchPathProxyHandler implements PathGenerator, InvocationHandler {

  private static final Joiner FIELD_JOINER = Joiner.on('.')

  private final PropertyNamingStrategy namingStrategy
  private List<String> path = new ArrayList<>()

  ElasticSearchPathProxyHandler(PropertyNamingStrategy namingStrategy) {
    this.namingStrategy = namingStrategy
  }

  @Override
  Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if(method.returnType.isPrimitive() || method.returnType == String) {
      if(method.returnType == Void.class) {
        throw new RuntimeException('Void leaf not supported.')
      }

      path.add(encodeMethodName(method.name))
      return null
    }

    if(method.returnType.isArray()) {
      if(method.returnType.componentType.isPrimitive()) {
        if(method.returnType.componentType == Void.class) {
          throw new RuntimeException('Array of voids leaf not supported.')
        }

        path.add(encodeMethodName(method.name))
        return null
      }
    }

    if(Collection.isAssignableFrom(method.returnType)) {
      if(method.returnType.typeParameters.length != 1) {
        throw new RuntimeException('Invalid collection type.')
      }
      final Class<?> genericDeclaration = method.returnType.typeParameters[0].genericDeclaration
      if(genericDeclaration.isPrimitive()) {
        if(genericDeclaration == Void.class) {
          throw new RuntimeException('Collection of voids leaf not supported.')
        }

        path.add(encodeMethodName(method.name))
        return null
      }
    }

    path.add(encodeMethodName(method.name))
    return Proxy.newProxyInstance(method.returnType.classLoader, [method.returnType] as Class<?>[], this)
  }

  private String encodeMethodName(String name) {
    if(name.startsWith('get') && name.length() > 3) {
      name = namingStrategy.nameForGetterMethod(null, null, name.substring(3))
    }

    return name
  }

  @Override
  String getPath() {
    return FIELD_JOINER.join(path)
  }
}
