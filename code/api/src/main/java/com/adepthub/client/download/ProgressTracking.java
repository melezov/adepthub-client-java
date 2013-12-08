package com.adepthub.client.download;

public interface ProgressTracking {
  public static interface ProgressListener {
    public void contentLengthUpdated(
        final StreamDefinition streams,
        final long contentLength);
  }

  public void registerListener(final ProgressListener listener);
}
