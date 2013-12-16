package com.adepthub.client.hash;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;

public class Hasher {
  private final Logger logger;

  public Hasher(
      final Logger logger) {
    this.logger = logger;
  }

  public HashProcess init() {
    return new HashProcess();
  }

  public class HashProcess {
    private final MessageDigest sha256;

    private HashProcess() {
      try {
        sha256 = MessageDigest.getInstance("SHA-256");
      } catch (final NoSuchAlgorithmException e) {
        /*
         Should not happen, as via:
         http://docs.oracle.com/javase/7/docs/api/java/security/MessageDigest.html
        */
        throw new RuntimeException(
            "Could not initialize the SHA-256 message digest", e);
      }
    }

    public void update(
        final byte input[],
        final int offset,
        final int len) {
      sha256.update(input, offset, len);
    }

    public SHA256 digest() {
      final SHA256 hash = new SHA256(sha256.digest());
      logger.debug("Digested hash: {}", hash);
      return hash;
    }
  }

  private static final int BUFFER_SIZE = 8192;

  public boolean validateStream(final InputStream is, final SHA256 hash) throws IOException {
    final BufferedInputStream bis = new BufferedInputStream(is);
    final byte[] buffer = new byte[BUFFER_SIZE];
    final HashProcess process = new HashProcess();

    while (true) {
      final int read = bis.read(buffer);
      if (read == -1) break;
      process.update(buffer, 0, read);
    }

    return hash.validate(process.digest());
  }

  public boolean validateFile(final File file, final SHA256 hash) throws IOException {
    final InputStream is = new FileInputStream(file);
    try {
      return validateStream(is, hash);
    }
    finally {
      is.close();
    }
  }
}
