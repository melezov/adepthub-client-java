package com.adepthub.client.hash;

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
}
