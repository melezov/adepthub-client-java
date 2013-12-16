package com.adepthub.client.download;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;

import com.adepthub.client.download.impl.StreamingDownloadMultiple;
import com.adepthub.client.download.impl.StreamingDownloadSingle;
import com.adepthub.client.hash.Hasher;
import com.adepthub.client.hash.SHA256;

public class StreamingDownload {
  private final Logger logger;
  private final Hasher hasher;

  public StreamingDownload(
      final Logger logger,
      final Hasher hasher) {
    this.logger = logger;
    this.hasher = hasher;
  }

  public DownloadProcess download(
      final List<StreamDefinition> streamsList,
      final long expectedSize,
      final SHA256 expectedHash,
      final ExecutorService executorService) {

    if (streamsList.size() == 1) {
      return new StreamingDownloadSingle(logger, hasher, streamsList.get(0),
          expectedSize, expectedHash);
    } else {
      return new StreamingDownloadMultiple(logger, hasher, streamsList,
          expectedSize, expectedHash, executorService);
    }
  }
}
