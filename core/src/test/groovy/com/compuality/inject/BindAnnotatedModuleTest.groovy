package com.compuality.inject

import com.google.inject.Binder
import com.google.inject.Key
import com.google.inject.Module
import com.google.inject.binder.AnnotatedBindingBuilder
import com.google.inject.binder.LinkedBindingBuilder
import com.google.inject.name.Named
import org.junit.Test

import static org.mockito.Matchers.anyObject
import static org.mockito.Matchers.anyVararg
import static org.mockito.Matchers.eq
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.times
import static org.mockito.Mockito.when
import static org.mockito.Mockito.verify

class BindAnnotatedModuleTest {

  @Test
  void testConfigure() {
    Module module = BindAnnotatedModule.fromInstance(new AnnotatedObject())

    Binder binder1 = mock(Binder)
    LinkedBindingBuilder linkedBindingBuilder = mock(LinkedBindingBuilder)
    AnnotatedBindingBuilder annotatedBindingBuilder1 = mock(AnnotatedBindingBuilder)
    when(annotatedBindingBuilder1.annotatedWith((Class)anyObject())).thenReturn(linkedBindingBuilder)
    when(binder1.bind((Class)anyObject())).thenReturn(annotatedBindingBuilder1)
    Binder binder2 = mock(Binder)
    AnnotatedBindingBuilder annotatedBindingBuilder2 = mock(AnnotatedBindingBuilder)
    when(binder2.bind((Key)anyObject())).thenReturn(annotatedBindingBuilder2)
    when(binder1.skipSources((Class)anyVararg())).thenReturn(binder2)

    module.configure(binder1)

    verify(binder1, times(1)).bind((Class)eq(Implementation))
    verify(binder1, times(1)).bind((Class)eq(InterfaceA))
    verify(binder1, times(1)).bind((Class)eq(InterfaceB))
    verify(binder1, times(5)).bind((Class)eq(ChildImplementation))
    verify(annotatedBindingBuilder1, times(7)).toInstance(anyObject())
    verify(annotatedBindingBuilder1, times(1)).annotatedWith((Class)anyObject())
    verify(linkedBindingBuilder, times(1)).toInstance(anyObject())
    verify(annotatedBindingBuilder2, times(1)).toInstance(anyObject())
    verify(binder2, times(1)).bind((Key)anyObject())
  }

  static class AnnotatedObject {

    Implementation notAnnotatedImplementation = new Implementation()

    InterfaceA notAnnotatedInterface = new Implementation()

    @Bind
    Implementation nullAnnotatedImplementation

    @Bind
    InterfaceA nullAnnotatedInterface

    @Bind(annotatedWith = Named)
    Implementation annotatedImplementation = new Implementation()

    @Bind
    @Multibind(bindClass = InterfaceC)
    InterfaceA annotatedInterface = new Implementation()

    @Bind(bindClass = InterfaceB)
    Implementation annotatedImplementationWithBindClassOverride = new Implementation()
  }

  static interface InterfaceA {

  }

  static interface InterfaceB {

  }

  static interface InterfaceC {

  }

  static class ChildImplementation {

  }

  static class Implementation implements InterfaceA, InterfaceB, InterfaceC {

    @Bind
    ChildImplementation childImplementation = new ChildImplementation()
  }
}
