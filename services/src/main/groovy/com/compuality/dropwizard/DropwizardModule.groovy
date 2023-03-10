package com.compuality.dropwizard

import com.compuality.inject.WebService
import com.google.inject.AbstractModule
import com.google.inject.multibindings.Multibinder
import com.yammer.dropwizard.config.Environment
import com.yammer.dropwizard.tasks.Task
import com.yammer.metrics.core.HealthCheck

import javax.inject.Inject

class DropwizardModule extends AbstractModule {

  private final Object config
  private final Environment env

  DropwizardModule(Object config, Environment env) {
    this.config = config
    this.env = env
  }

  @Override
  protected void configure() {
    bind(Environment).toInstance(env)

    // Create multibinders so there is at least an empty set available for Guice to inject.
    Multibinder<Object> resourceBinder = Multibinder.newSetBinder(binder(), WebService)
    Multibinder<Task> taskBinder = Multibinder.newSetBinder(binder(), Task)
    Multibinder<HealthCheck> healthCheckBinder = Multibinder.newSetBinder(binder(), HealthCheck)

    bind(ResourceCollector).asEagerSingleton()
    bind(TaskCollector).asEagerSingleton()
    bind(HealthCheckCollector).asEagerSingleton()
  }

  private static class ResourceCollector {

    @Inject
    ResourceCollector(Environment env, Set<WebService> resources) {
      for(WebService r : resources) {
        env.addResource(r)
      }
    }
  }

  private static class TaskCollector {

    @Inject
    TaskCollector(Environment env, Set<Task> tasks) {
      for(Task t : tasks) {
        env.addTask(t)
      }
    }
  }

  private static class HealthCheckCollector {

    @Inject
    HealthCheckCollector(Environment env, Set<HealthCheck> healthChecks) {
      for(HealthCheck h : healthChecks) {
        env.addHealthCheck(h)
      }
    }
  }
}
