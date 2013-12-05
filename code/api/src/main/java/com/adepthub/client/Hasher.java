package com.adepthub.client;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;

public class Hasher {
  private final Logger logger;
  private final MessageDigest sha256;

  public Hasher(
      final Logger logger) {
    this.logger = logger;

    try {
      sha256 = MessageDigest.getInstance("SHA-256");
    }
    catch (final NoSuchAlgorithmException e) {
      /*
       Should not happen, as via:
       http://docs.oracle.com/javase/7/docs/api/java/security/MessageDigest.html
      */
      throw new RuntimeException("Could not initialize the SHA-256 message digest", e);
    }
  }

  public final byte[] digest(
      final byte[] content) {

    final byte[] hash = sha256.digest(content);

    if (logger.isTraceEnabled()) {
      logger.trace("Digested input of {} bytes: {}", content.length, bytesToHex(hash));
    }

    return hash;
  }

  public static byte[] hexToBytes(final String hashString) {
    return DatatypeConverter.parseHexBinary(hashString);
  }

  // Will return an uppercase hex representation of the hash
  public static String bytesToHex(final byte[] hash) {
    return DatatypeConverter.printHexBinary(hash);
  }
}
