package com.adepthub.client.download;

import java.io.File;

public class StreamDefinition {
  public final String location;
  public final File output;

  public StreamDefinition(
      final String location,
      final File output) {
    this.location = location;
    this.output = output;
  }
}
