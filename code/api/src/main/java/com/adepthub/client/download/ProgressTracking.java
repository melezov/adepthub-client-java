package com.adepthub.client.download;

public interface ProgressTracking {
  public static interface ProgressListener {
    public void sizeUpdated(
        final StreamDefinition streams,
        final long size);
  }

  public void registerListener(final ProgressListener listener);
}
