package com.compuality.interfaces

import com.google.common.base.Optional

public interface Identifiable {

  Optional<String> tryGetId()
}