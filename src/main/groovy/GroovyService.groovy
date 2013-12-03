import com.google.inject.Guice
import com.yammer.dropwizard.Service
import com.yammer.dropwizard.config.Bootstrap
import com.yammer.dropwizard.config.Configuration
import com.yammer.dropwizard.config.Environment

class GroovyService extends Service<Configuration> {

    @Override
    void initialize(Bootstrap<Configuration> bootstrap) {

    }

    @Override
    void run(Configuration configuration, Environment environment) throws Exception {
//        environment.addResource(new GroovyResource());
        Guice.createInjector(new GroovyModule(environment));
    }

    public static void main(String[] args) throws Exception {
        new GroovyService().run(args);
    }
}

