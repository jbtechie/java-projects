package com.compuality.dropwizard
import com.compuality.ServerConfiguration
import com.compuality.elasticsearch.ElasticSearchModule
import com.compuality.experiment.ExperimentModule
import com.google.inject.Guice
import com.google.inject.Module
import com.yammer.dropwizard.Service
import com.yammer.dropwizard.assets.AssetsBundle
import com.yammer.dropwizard.config.Bootstrap
import com.yammer.dropwizard.config.Environment
import com.yammer.dropwizard.views.ViewBundle

class MainService extends Service<ServerConfiguration> {

    private List<Module> modules = [ new ElasticSearchModule(),
                                     new ExperimentModule() ]

    @Override
    void initialize(Bootstrap<ServerConfiguration> bootstrap) {
      bootstrap.addBundle(new ViewBundle())
      bootstrap.addBundle(new AssetsBundle("/assets/", "/"));
    }

    @Override
    void run(ServerConfiguration config, Environment env) throws Exception {
      modules.add(new DropwizardModule(config, env))
      Guice.createInjector(modules)
    }

    public static void main(String[] args) throws Exception {
        new MainService().run(args);
    }
}

