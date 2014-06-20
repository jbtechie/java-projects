package com.compuality.sandbox.ui

import com.compuality.services.ui.View
import com.google.inject.AbstractModule

class ViewModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(View)
  }
}
