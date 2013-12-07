package com.adepthub.writer.pathgenerator;

import java.io.File;

import com.adepthub.client.Hasher;

public class TwoFolderHomeGenerator implements FilePathGenerator {

  private static final String homeFolderPath  = System.getProperty("user.home");

  @Override
  public String getFilePath(byte[] hash) {
    final String hashString     = Hasher.bytesToHex(hash);
    final StringBuilder builder = new StringBuilder();

    builder.append(homeFolderPath)
           .append(File.separator)
           .append(hashString.substring(0, 4))
           .append(File.separator)
           .append(hashString.substring(4, 8))
           .append(File.separator)
           .append(hashString.substring(8));

    return builder.toString();
  }


}
