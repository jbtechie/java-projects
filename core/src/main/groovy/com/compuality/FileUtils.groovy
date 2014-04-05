package com.compuality

import com.google.common.base.Joiner

class FileUtils {

  private static final Joiner PATH_JOINER = Joiner.on(File.separator)

  public static Joiner pathJoiner() {
    return PATH_JOINER
  }

  private FileUtils
}
