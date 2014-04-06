package com.compuality.sandbox.main
import com.compuality.collection.CollectionAppender
import com.compuality.dropwizard.DropwizardModule

import com.compuality.inject.BindAnnotatedModule
import com.compuality.service.ServicesModule
import com.google.inject.Guice
import com.google.inject.Module
import com.yammer.dropwizard.Service
import com.yammer.dropwizard.assets.AssetsBundle
import com.yammer.dropwizard.config.Bootstrap
import com.yammer.dropwizard.config.Environment

import static com.compuality.collection.impl.CollectionAppenderImpl.listAppender

class MainDropwizardApp extends Service<MainDropwizardAppConfig> {

  private final CollectionAppender<Module> moduleRegistry = listAppender()

  @Override
  void initialize(Bootstrap<MainDropwizardAppConfig> bootstrap) {
    bootstrap.addBundle(new AssetsBundle('/com/compuality/sandbox/web/ui', '/'))
  }

  @Override
  void run(MainDropwizardAppConfig config, Environment env) throws Exception {

    moduleRegistry.add(new BindAnnotatedModule(config))
    moduleRegistry.add(new DropwizardModule(config, env))
    moduleRegistry.add(new ServicesModule())

    Guice.createInjector(moduleRegistry.items())
  }

  static void main(String... args) {
    new MainDropwizardApp().run(args)
  }
}