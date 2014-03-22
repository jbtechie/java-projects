package com.compuality.learn.guice
import com.google.inject.*
import com.google.inject.multibindings.Multibinder
import org.junit.Test

class GuiceBindingsLearnTests {

  @Test
  void bindInterface() {
    Module module = new AbstractModule() {
      @Override
      protected void configure() {
        bind(SubInterface).to(Implementation).in(Scopes.SINGLETON)
        bind(MostSuperInterface).to(SubInterface)
      }
    }

    Injector injector = Guice.createInjector(module);
    injector.getBinding(MostSuperInterface)
  }

  @Test
  void multibindInterface() {
    Module module = new AbstractModule() {
      @Override
      protected void configure() {
        bind(SubInterface).to(Implementation).in(Scopes.SINGLETON)

        Multibinder<MostSuperInterface> mostBinder = Multibinder.newSetBinder(binder(), MostSuperInterface);
        mostBinder.addBinding().to(SubInterface)
      }
    }

    Injector injector = Guice.createInjector(module);
    assert injector.findBindingsByType(new TypeLiteral<Set<MostSuperInterface>>() {}).size() == 1
  }

  static interface MostSuperInterface {

  }

  static interface SubInterface extends MostSuperInterface {

  }

  static class Implementation implements SubInterface {

  }

//  @BindingAnnotation @Target([ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD ]) @Retention(RetentionPolicy.RUNTIME)
//  public @interface MyBindingAnnotation {}
}
