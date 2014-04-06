package com.compuality.service
import com.compuality.inject.WebService
import com.google.inject.AbstractModule
import com.google.inject.Scopes
import com.google.inject.multibindings.Multibinder

class WebServiceModule extends AbstractModule {

  private final List<Class<? extends WebService>> services

  WebServiceModule(Class<? extends WebService>... services) {
    this.services = services.toList()
  }

  WebServiceModule(List<Class<? extends WebService>> services) {
    this.services = services
  }

  @Override
  protected void configure() {
    Multibinder<WebService> servicesBinder = Multibinder.newSetBinder(binder(), WebService)
    services.each { servicesBinder.addBinding().to(it).in(Scopes.SINGLETON) }
  }
}
