package com.compuality.config

import com.google.inject.AbstractModule

class ConfigModule extends AbstractModule {

  private final Object config

  static ConfigModule fromInstance(Object config) {
    return new ConfigModule(config)
  }

  static ConfigModule fromResource(Class<?> configClass) {

  }

  static ConfigModule fromResource(Class<?> configClass, String resourceName) {

  }

  static ConfigModule fromFile(Class<?> configClass, File configFile) {

  }

  private ConfigModule(Object config) {
    this.config = config
  }

  @Override
  protected void configure() {

  }
}
