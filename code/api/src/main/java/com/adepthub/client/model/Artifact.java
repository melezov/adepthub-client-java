package com.adepthub.client.model;

import com.adepthub.client.hash.SHA256;

public class Artifact {
  public final String[] locations;
  public final long contentLength;
  public final SHA256 hash;
  public final String filename;

  public Artifact(
      final String[] locations,
      final long contentLength,
      final SHA256 hash,
      final String filename) {
    this.locations = locations;
    this.contentLength = contentLength;
    this.hash = hash;
    this.filename = filename;
  }
}
