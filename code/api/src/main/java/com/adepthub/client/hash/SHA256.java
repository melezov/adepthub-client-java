package com.adepthub.client.hash;

import java.util.Arrays;

import javax.xml.bind.DatatypeConverter;

public final class SHA256 {
  private final byte[] hashBytes;  // 32 byte binary
  private final String hashString; // hex encoded lowercase string, 64 chars

  public SHA256(
      final byte[] hash) {
    assert (hash.length == 32);
    this.hashBytes = hash;
    this.hashString = bytesToHex(hashBytes);
  }

  public SHA256(
      final String hash) {
    this(hexToBytes(hash));
  }

  // ---------------------------------------------------------------------------

  @Override
  public int hashCode() {
    return hashString.hashCode();
  }

  @Override
  public boolean equals(final Object o) {
    return (o instanceof SHA256) && validate((SHA256) o);
  }

  @Override
  public String toString() {
    return hashString;
  }

  // ---------------------------------------------------------------------------

  public boolean validate(final SHA256 hash) {
    return validate(hash.hashBytes);
  }

  public boolean validate(final byte[] hashBytes) {
    return Arrays.equals(this.hashBytes, hashBytes);
  }

  public boolean validate(final String hashString) {
    return this.hashString.equalsIgnoreCase(hashString);
  }

  // ---------------------------------------------------------------------------
  public byte[] getHashBytes() {
    return hashBytes.clone();
  }

  public String getHashString() {
    return hashString;
  }

  // ---------------------------------------------------------------------------

  // Converts a hex representation of the hash into binary form
  private static byte[] hexToBytes(final String hashString) {
    return DatatypeConverter.parseHexBinary(hashString);
  }

  // Will return a lowercase hex representation of the hash
  private static String bytesToHex(final byte[] hash) {
    return DatatypeConverter.printHexBinary(hash).toLowerCase();
  }
}
