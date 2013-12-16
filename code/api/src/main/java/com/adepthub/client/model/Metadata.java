package com.adepthub.client.model;

public class Metadata {
  public final String[] organization;
  public final String[] name;
  public final String[] version;

  public Metadata(
      final String[] organization,
      final String[] name,
      final String[] version) {
    this.organization = organization;
    this.name = name;
    this.version = version;
  }
}
