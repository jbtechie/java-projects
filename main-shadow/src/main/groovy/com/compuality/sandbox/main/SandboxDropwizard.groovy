package com.compuality.sandbox.main

import com.compuality.collection.CollectionAppender
import com.compuality.dropwizard.DropwizardModule
import com.google.inject.Guice
import com.google.inject.Module
import com.yammer.dropwizard.Service
import com.yammer.dropwizard.config.Bootstrap
import com.yammer.dropwizard.config.Environment

import static com.compuality.collection.impl.CollectionAppenderImpl.listAppender

class SandboxDropwizard extends Service<SandboxConfiguration> {

  private final CollectionAppender<Module> moduleRegistry = listAppender()

  @Override
  void initialize(Bootstrap<SandboxConfiguration> bootstrap) {
  }

  @Override
  void run(SandboxConfiguration config, Environment env) throws Exception {

    moduleRegistry.add(new DropwizardModule(config, env))

    Guice.createInjector(moduleRegistry.items())
  }

  static void main(String... args) {
    new SandboxDropwizard().run(args)
  }
}