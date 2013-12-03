import com.google.inject.AbstractModule
import com.yammer.dropwizard.config.Environment

class GroovyModule extends AbstractModule {

    def env

    GroovyModule(Environment env) {
        this.env = env
    }

    @Override
    protected void configure() {
        bind(Random.class).toInstance(new Random(0))
        bind(Environment.class).toInstance(env)
        bind(GroovyResource.class).asEagerSingleton()
    }

}
