package com.compuality.dropwizard

import com.compuality.Configuration
import com.compuality.ServerConfiguration
import com.compuality.dropwizard.views.ViewResource
import com.google.inject.AbstractModule
import com.yammer.dropwizard.config.Environment

class DropwizardModule extends AbstractModule {

  private final ServerConfiguration config
  private final Environment env

  DropwizardModule(ServerConfiguration config, Environment env) {
    this.config = config
    this.env = env
  }

  @Override
  protected void configure() {
    bind(ServerConfiguration).toInstance(config)

    ServerConfiguration.declaredMethods.each {
      if(it.getAnnotation(Configuration) && it.parameterTypes.length == 0 && it.returnType != Void) {
        bind(it.returnType).toInstance(it.invoke(ServerConfiguration))
      }
    }

    bind(Random.class).toInstance(new Random(0))
    bind(Environment.class).toInstance(env)
    bind(GroovyResource.class).asEagerSingleton()
    bind(ViewResource.class).asEagerSingleton()
  }
}
