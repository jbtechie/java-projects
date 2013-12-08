import com.google.inject.Inject
import com.yammer.dropwizard.config.Environment
import views.GroovyView

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/view")
@Produces(MediaType.TEXT_HTML)
class ViewResource {

  @Inject
  GroovyResource(Environment env) {
    env.addResource(this);
  }

  @GET
  GroovyView get() {
    return new GroovyView()
  }
}
