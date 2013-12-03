import com.google.inject.Inject
import com.yammer.dropwizard.config.Environment

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
class GroovyResource {

    def rand

    @Inject
    GroovyResource(Environment env, Random rand) {
        env.addResource(this);
        this.rand = rand
    }

    @GET
    def groovy() {
        return [rand: rand.nextInt()]
    }

    static class GroovyResponse {
        String message
    }
}
