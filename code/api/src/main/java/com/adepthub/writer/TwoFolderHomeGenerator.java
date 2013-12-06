package com.adepthub.writer;

import java.io.File;

import com.adepthub.client.Hasher;

public class TwoFolderHomeGenerator implements FilePathGenerator {

  private final static String homeFolderPath  = System.getProperty("user.home");

  @Override
  public String getFilePath(byte[] hash) {
    final String hashString     = Hasher.bytesToHex(hash);
    final StringBuilder builder = new StringBuilder();

    builder.append(homeFolderPath)
           .append(File.pathSeparator)
           .append(hashString.substring(0, 4))
           .append(File.pathSeparator)
           .append(hashString.substring(4, 8))
           .append(File.pathSeparator)
           .append(hashString.substring(8));

    return builder.toString();
  }


}
