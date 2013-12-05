package com.adepthub.client.model;

public class Entry {
  public final String url;
  public final Artifact[] artifacts;

  public Entry(
      final String url,
      final Artifact[] artifacts) {
    this.url = url;
    this.artifacts = artifacts;
  }
}
