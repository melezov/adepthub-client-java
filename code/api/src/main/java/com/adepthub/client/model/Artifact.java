package com.adepthub.client.model;

import com.adepthub.client.hash.SHA256;

public class Artifact {
  public final String[] locations;
  public final SHA256 hash;
  public final long size;
  public final String filename;
  public final Metadata metadata;

  public Artifact(
      final String[] locations,
      final SHA256 hash,
      final long size,
      final String filename,
      final Metadata metadata) {
    this.locations = locations;
    this.hash = hash;
    this.size = size;
    this.filename = filename;
    this.metadata = metadata;
  }
}
