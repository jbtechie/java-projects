package com.compuality.dropwizard

import com.compuality.ServerConfiguration
import com.compuality.elasticsearch.ElasticSearchModule
import com.google.inject.Guice
import com.yammer.dropwizard.Service
import com.yammer.dropwizard.assets.AssetsBundle
import com.yammer.dropwizard.config.Bootstrap
import com.yammer.dropwizard.config.Environment
import com.yammer.dropwizard.views.ViewBundle

class GroovyService extends Service<ServerConfiguration> {

    @Override
    void initialize(Bootstrap<ServerConfiguration> bootstrap) {
      bootstrap.addBundle(new ViewBundle())
      bootstrap.addBundle(new AssetsBundle("/assets/", "/"));
    }

    @Override
    void run(ServerConfiguration config, Environment env) throws Exception {
        Guice.createInjector(new GroovyModule(config, env), new ElasticSearchModule());
    }

    public static void main(String[] args) throws Exception {
        new GroovyService().run(args);
    }
}

