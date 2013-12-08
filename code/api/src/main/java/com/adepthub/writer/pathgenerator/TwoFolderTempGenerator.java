/*
package com.adepthub.writer.pathgenerator;

import java.io.File;
import java.util.Random;

public class TwoFolderTempGenerator extends FilePathGeneratorDecorator {

  private final static String tempFolderPath  = System.getProperty("java.io.tmpdir");
  private final Random randomGenerator        = new Random(System.currentTimeMillis());

  public TwoFolderTempGenerator(FilePathGenerator generator) {
    super(generator);
  }

  @Override
  public String getFilePath(byte[] hash) {
    undecoratedValue = generator.getFilePath(hash);
    return tempFolderPath
        + File.separator
        + undecoratedValue
        + String.valueOf(randomGenerator.nextInt());
  }

}
*/
