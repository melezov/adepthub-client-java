package com.adepthub.writer.pathgenerator;

import java.io.File;

public class TwoFolderHomeGenerator extends FilePathGeneratorDecorator {

  private static final String homeFolderPath = System.getProperty("user.home");

  public TwoFolderHomeGenerator(FilePathGenerator generator) {
    super(generator);
  }

  @Override
  public String getFilePath(byte[] hash) {
    undecoratedValue = generator.getFilePath(hash);
    return homeFolderPath + File.separator + undecoratedValue;
  }

}