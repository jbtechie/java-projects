import com.google.inject.AbstractModule
import com.yammer.dropwizard.config.Configuration
import com.yammer.dropwizard.config.Environment

class GroovyModule extends AbstractModule {

  def config
  def env

  GroovyModule(Configuration config, Environment env) {
    this.config = config
    this.env = env
  }

  @Override
  protected void configure() {
    bind(Random.class).toInstance(new Random(0))
    bind(Environment.class).toInstance(env)
    bind(GroovyResource.class).asEagerSingleton()
    bind(ViewResource.class).asEagerSingleton()
  }
}
