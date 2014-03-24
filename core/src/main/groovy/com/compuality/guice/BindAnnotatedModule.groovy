package com.compuality.guice
import com.google.inject.AbstractModule
import com.google.inject.binder.AnnotatedBindingBuilder
import com.google.inject.multibindings.Multibinder

import java.lang.annotation.Annotation

class BindAnnotatedModule extends AbstractModule {

  static BindAnnotatedModule fromInstance(Object root) {
    return new BindAnnotatedModule(root)
  }

  private final Object root

  private BindAnnotatedModule(Object root) {
    this.root = root
  }

  @Override
  protected void configure() {
    bindRecursively(root)
  }

  private void bindRecursively(Object object) {

    final Map<String, PropertyValue> propertyMap = object.metaPropertyValues.collectEntries { [it.name, it] }

    object.class.declaredFields.each { final field ->
      final Bind bindAnnotation = field.getAnnotation(Bind)
      final Multibind multibindAnnotation = field.getAnnotation(Multibind)
      final PropertyValue property = propertyMap.get(field.getName())

      if(property && property.value) {

        if(bindAnnotation) {
          final Class<?> bindClass = bindAnnotation.bindClass().equals(void) ? property.type : bindAnnotation.bindClass()
          final AnnotatedBindingBuilder binding = bind(bindClass)
          if(bindAnnotation.annotatedWith().equals(Annotation)) {
            binding.toInstance(property.value)
          } else {
            binding.annotatedWith(bindAnnotation.annotatedWith()).toInstance(property.value)
          }
        }

        if(multibindAnnotation) {
          final Class<?> bindClass = multibindAnnotation.bindClass().equals(void) ? property.type : multibindAnnotation.bindClass()
          final Multibinder<?> multibinder = multibindAnnotation.annotatedWith().equals(Annotation) ? Multibinder.newSetBinder(binder(), bindClass)
              : Multibinder.newSetBinder(binder(), bindClass, multibindAnnotation.annotatedWith())
          multibinder.addBinding().toInstance(property.value)
        }

        bindRecursively(property.value)
      }
    }
  }
}
