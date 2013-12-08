import com.google.inject.Guice
import com.yammer.dropwizard.Service
import com.yammer.dropwizard.assets.AssetsBundle
import com.yammer.dropwizard.config.Bootstrap
import com.yammer.dropwizard.config.Configuration
import com.yammer.dropwizard.config.Environment
import com.yammer.dropwizard.views.ViewBundle

class GroovyService extends Service<Configuration> {

    @Override
    void initialize(Bootstrap<Configuration> bootstrap) {
      bootstrap.addBundle(new ViewBundle())
      bootstrap.addBundle(new AssetsBundle("/assets/", "/"));
    }

    @Override
    void run(Configuration config, Environment env) throws Exception {
//        environment.addResource(new GroovyResource());
        Guice.createInjector(new GroovyModule(config, env));
    }

    public static void main(String[] args) throws Exception {
        new GroovyService().run(args);
    }
}

