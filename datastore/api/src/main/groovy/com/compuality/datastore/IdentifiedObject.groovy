package com.compuality.datastore

import com.compuality.interfaces.Identified

public interface IdentifiedObject<T> extends Identified {

  T getObject()
}