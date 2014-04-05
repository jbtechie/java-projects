package com.compuality.resource

import org.apache.commons.lang3.ClassUtils

import static com.compuality.FileUtils.pathJoiner
import static com.google.common.base.Preconditions.checkNotNull

class ResourceUtils {

  static String getResource(Class<?> relativePathClass, String resourceName) {
    checkNotNull(relativePathClass)
    checkNotNull(resourceName)
    String basePath = ClassUtils.getPackageName(relativePathClass).replace('.', File.separator)
    return pathJoiner().join(basePath, resourceName)
  }

  private ResourceUtils() {}
}
