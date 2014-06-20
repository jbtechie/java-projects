package com.compuality.services.ui

import com.compuality.inject.WebService

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path('ui/view')
@Produces(MediaType.APPLICATION_JSON)
class View implements WebService {

  @GET
  List<Number> get() {
    return [3, 7, 13, 17, 23]
  }
}
