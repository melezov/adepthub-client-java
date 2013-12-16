package com.adepthub.client.download.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;

import com.adepthub.client.download.DownloadProcess;
import com.adepthub.client.download.Result;
import com.adepthub.client.download.StreamDefinition;
import com.adepthub.client.hash.Hasher;
import com.adepthub.client.hash.Hasher.HashProcess;
import com.adepthub.client.hash.SHA256;

public class StreamingDownloadSingle
    implements DownloadProcess {

  private final Logger logger;
  private final Hasher hasher;
  private final StreamDefinition streams;
  private final long expectedSize;
  private final SHA256 expectedHash;

  public StreamingDownloadSingle(
      final Logger logger,
      final Hasher hasher,
      final StreamDefinition streams,
      final long expectedSize,
      final SHA256 expectedHash) {
    this.logger = logger;
    this.hasher = hasher;
    this.streams = streams;
    this.expectedSize = expectedSize;
    this.expectedHash = expectedHash;
  }

  private static final int BUFFER_LENGTH = 4096;

  @Override
  public List<Result> call() throws Exception {
    final List<Result> results = new ArrayList<Result>(1);
    results.add(streamSingle());
    return results;
  }

  private Result streamSingle() throws Exception {
    final long startedAt = System.currentTimeMillis();

    try {
      final URL url = new URL(streams.location);
      final InputStream is =
          new BufferedInputStream(url.openStream(), BUFFER_LENGTH);
      logger.debug("Opened connection to {}", streams.location);

      try {
        streamToFile(is);
      } finally {
        is.close();
      }

      return new Result(
          streams,
          true,
          "Download successfully completed.",
          System.currentTimeMillis() - startedAt);
    } catch (final Exception e) {
      return new Result(
          streams,
          false,
          e.getMessage(),
          System.currentTimeMillis() - startedAt);
    }
  }

  private void streamToFile(final InputStream is) throws Exception {

    final OutputStream os = new BufferedOutputStream(
        new FileOutputStream(streams.output), BUFFER_LENGTH);
    logger.debug("Streaming output to {}", streams.output);

    try {
      pipeStream(is, os);
      os.close();
    } catch (final Exception e) {
      os.close();
      streams.output.delete();
      throw e;
    }
  }

  private void pipeStream(
      final InputStream is,
      final OutputStream os) throws IOException {

    final byte[] buffer = new byte[BUFFER_LENGTH];
    final HashProcess hashProcess = hasher.init();

    long size = 0L;

    while (true) {
      if (canceled) {
        logger.debug("Canceled was true, interrupting download process ...");
        throw new IOException("Download process was interrupted.");
      }

      final int read = is.read(buffer);
      if (read == -1) break;

      if (logger.isTraceEnabled()) {
        logger.trace(String.format("Read %4d bytes from %s", read,
            streams.location));
      }

      if (size + read > expectedSize) {
        logger.debug(
            "Retrieved more than expected {} bytes, halting download procedure ...",
            expectedSize);
        throw new IOException(String.format(
            "Download was too large, only %d bytes were expected",
            expectedSize));
      }

      os.write(buffer, 0, read);
      hashProcess.update(buffer, 0, read);
      size += read;

      notifyListeners(streams, size);
    }

    // Download complete, performing size validation
    if (size < expectedSize) {
      throw new IOException(String.format(
          "Downloaded only %d bytes, but %d were expected", size,
          expectedSize));
    }

    // Checking hash
    final SHA256 calculatedHash = hashProcess.digest();
    if (!expectedHash.validate(calculatedHash)) {
      throw new IOException(String.format(
          "Checksum of the downloaded file %s was different than the expected one %s",
          calculatedHash,
          expectedHash));
    }

    logger.debug("Successfully downloaded and verified: {}", streams.location);
  }

  // ---------------------------------------------------------------------------

  protected volatile boolean canceled;

  @Override
  public void cancel() {
    // write only, no synchronization needed with reads from local thread
    canceled = true;
  }

  // ---------------------------------------------------------------------------

  protected final Set<ProgressListener> progressListeners =
      new LinkedHashSet<ProgressListener>();

  @Override
  public void registerListener(final ProgressListener listener) {
    progressListeners.add(listener);
  }

  private void notifyListeners(
      final StreamDefinition streams,
      final long size) {
    for(final ProgressListener listener : progressListeners) {
      listener.sizeUpdated(streams, size);
    }
  }
}
