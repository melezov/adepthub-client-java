/*
package com.adepthub.writer.pathgenerator;

import java.io.File;

import com.adepthub.client.Hasher;

public class TwoFolderGenerator implements FilePathGenerator {

  @Override
  public String getFilePath(byte[] hash) {
    final String hashString = Hasher.bytesToHex(hash);

    return hashString.substring(0, 4)
         + File.pathSeparator
         + hashString.substring(4, 8)
         + File.pathSeparator
         + hashString.substring(8);
  }

}
*/
