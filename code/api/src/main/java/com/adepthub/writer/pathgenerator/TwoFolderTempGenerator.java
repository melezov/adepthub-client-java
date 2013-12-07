package com.adepthub.writer.pathgenerator;

import java.io.File;
import java.util.Random;

import com.adepthub.client.Hasher;

public class TwoFolderTempGenerator implements FilePathGenerator {

  private final static String tempFolederPath = System.getProperty("java.io.tmpdir");
  private final Random randomGenerator        = new Random(System.currentTimeMillis());

  @Override
  public String getFilePath(byte[] hash) {
    final String hashString     = Hasher.bytesToHex(hash);
    final StringBuilder builder = new StringBuilder();

    builder.append(tempFolederPath)
           .append(File.separator)
           .append(hashString.substring(0, 4))
           .append(File.separator)
           .append(hashString.substring(4, 8))
           .append(File.separator)
           .append(hashString.substring(8))
           .append(String.valueOf(randomGenerator.nextInt()));

    return builder.toString();
  }

}
