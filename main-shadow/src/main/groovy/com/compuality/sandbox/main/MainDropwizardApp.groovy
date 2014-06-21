package com.compuality.sandbox.main

import com.compuality.collection.CollectionAppender
import com.compuality.dropwizard.DropwizardModule
import com.compuality.inject.BindAnnotatedModule
import com.compuality.service.ServicesModule
import com.compuality.service.WebServiceModule
import com.compuality.services.ui.JavaMathService
import com.compuality.services.ui.MathService
import com.compuality.services.ui.ViewService
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
        .add(new DropwizardModule(config, env))
        .add(new ServicesModule())
        .add(new WebServiceModule(ViewService))
        .add(new WebServiceModule(MathService))
        .add(new WebServiceModule(JavaMathService))

    Guice.createInjector(moduleRegistry.items())
  }

  static void main(String... args) {
    new MainDropwizardApp().run(args)
  }
}