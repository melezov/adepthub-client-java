package com.adepthub.client.download.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.slf4j.Logger;

import com.adepthub.client.download.DownloadProcess;
import com.adepthub.client.download.ProgressTracking.ProgressListener;
import com.adepthub.client.download.Result;
import com.adepthub.client.download.StreamDefinition;
import com.adepthub.client.hash.Hasher;
import com.adepthub.client.hash.SHA256;

public class StreamingDownloadMultiple
    implements DownloadProcess, ProgressListener {

  private final Logger logger;
  private final Hasher hasher;
  private final List<StreamDefinition> streamsList;
  private final long expectedSize;
  private final SHA256 expectedHash;
  private final ExecutorService executorService;

  public StreamingDownloadMultiple(
      final Logger logger,
      final Hasher hasher,
      final List<StreamDefinition> streamsList,
      final long expectedSize,
      final SHA256 expectedHash,
      final ExecutorService executorService) {
    this.logger = logger;
    this.hasher = hasher;
    this.streamsList = streamsList;
    this.expectedSize = expectedSize;
    this.expectedHash = expectedHash;
    this.executorService = executorService;

    this.downloads = new LinkedHashMap<DownloadProcess, Future<List<Result>>>();
  }

  private static final int POLLING_INTERVAL = 100; // ms

  private final Map<DownloadProcess, Future<List<Result>>> downloads;

  @Override
  public List<Result> call() throws Exception {
    downloads.clear();

    // we initialize each download on the provided executor service
    for (final StreamDefinition streams : streamsList) {
      final DownloadProcess download = new StreamingDownloadSingle(logger,
          hasher, streams, expectedSize, expectedHash);

      downloads.put(download, executorService.submit(download));
    }

    // wait until first download is successful, or all have failed
    final DownloadProcess firstSuccess = waitForFirstSuccess();

    // if at least one process has successfully finished, declare others canceled
    if (firstSuccess != null) {
      for (final DownloadProcess download : downloads.keySet()) {
        if (download != firstSuccess) {
          download.cancel();
        }
      }
    }

    // aggregate the results, a naive approach with which we could do without,
    // bet needed in the current implementation in order to perform output cleanup
    final List<Result> results = new ArrayList<Result>();
    for (final Future<List<Result>> futureResult : downloads.values()) {
      results.add(futureResult.get().get(0));
    }
    return results;
  }

  private DownloadProcess waitForFirstSuccess() {

    // kingdom for a @tailrec!
    while (true) {
      boolean allDone = true;
      boolean allCanceled = canceled;

      for (final Map.Entry<DownloadProcess, Future<List<Result>>> entry : downloads.entrySet()) {
        final DownloadProcess download = entry.getKey();
        final Future<List<Result>> futureResult = entry.getValue();

        // if all processes are done, but none succeeded, declare failure
        final boolean done = futureResult.isDone();
        if (!done) allDone = false;

        if (allCanceled) {
          download.cancel();
          continue;
        }

        try {
          if (futureResult.isDone() && futureResult.get().get(0).success) {
            return download;
          }
          Thread.sleep(POLLING_INTERVAL);
        } catch (final Exception e) {}
      }

      if (allDone || allCanceled) {
        return null;
      }
    }
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

  @Override
  public void sizeUpdated(
      final StreamDefinition streams,
      final long size) {
    for(final ProgressListener listener : progressListeners) {
      listener.sizeUpdated(streams, size);
    }
  }
}
