package com.compuality.resource

import com.google.common.io.Resources

class URIs {

  static URL toURL(URI uri) {
    if(uri.absolute) {
      return uri.toURL()
    } else {
      return Resources.getResource(uri.toString())
    }
  }
}
