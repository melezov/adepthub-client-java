package com.adepthub.client.model;

public class Artifact {
  public final String[] locations;
  public final byte[] hash;
  public final String filename;

  public Artifact(
      final String[] locations,
      final byte[] hash,
      final String filename) {
    this.locations = locations;
    this.hash = hash;
    this.filename = filename;
  }
}
