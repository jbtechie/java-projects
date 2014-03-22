package com.compuality.dropwizard
import com.compuality.BindConfiguration
import com.google.inject.AbstractModule
import com.google.inject.Inject
import com.yammer.dropwizard.config.Environment

class DropwizardModule extends AbstractModule {

  private final Object config
  private final Environment env

  DropwizardModule(Object config, Environment env) {
    this.config = config
    this.env = env
  }

  @Override
  protected void configure() {
    bindConfigsRecursively(config)
    bind(Environment).toInstance(env)
  }

  private void bindConfigsRecursively(Object config) {
    bind(config.class).toInstance(config)

    config.class.declaredFields.each {
      if(it.getAnnotation(BindConfiguration)) {
        it.accessible = true
        bindConfigsRecursively(it.get(config))
      }
    }
  }

  private static class EnvironmentConfigurator {

    @Inject
    EnvironmentConfigurator(Environment env) {
    }
  }
}
