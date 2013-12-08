package com.adepthub.client.download;

public class Result {
  public final StreamDefinition streams;
  public final boolean success;
  public final String message;
  public final long elapsedTime;

  public Result(
      final StreamDefinition streams,
      final boolean success,
      final String message,
      final long elapsedTime) {
    this.streams = streams;
    this.success = success;
    this.message = message;
    this.elapsedTime = elapsedTime;
  }
}
