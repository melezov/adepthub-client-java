package com.adepthub.client.download;

import java.io.File;

import com.adepthub.client.model.Artifact;

public class StreamDefinition {
  public final Artifact artifact;
  public final String location;
  public final File output;

  public StreamDefinition(
      final Artifact artifact,
      final String location,
      final File output) {
    this.artifact = artifact;
    this.location = location;
    this.output = output;
  }
}
