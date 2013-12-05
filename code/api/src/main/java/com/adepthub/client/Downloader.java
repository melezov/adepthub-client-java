package com.adepthub.client;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;

public class Downloader {
  private final Logger logger;
  private final Hasher hasher;
  private final ExecutorService executorService;

  public Downloader(
      final Logger logger,
      final Hasher hasher) {
    this.logger = logger;
    this.hasher = hasher;
    executorService = Executors.newCachedThreadPool();
  }

  private static final int BUFFER = 4096;

  public byte[] downloadAndVerify(
      final String[] locations,
      final byte[] hash) {

    final List<Callable<byte[]>> downloads =
        new ArrayList<Callable<byte[]>>();

    for (final String location : locations) {
      downloads.add(
          new Callable<byte[]>() {
            @Override
            public byte[] call() throws Exception {
              final URL url = new URL(location);
              final InputStream is = url.openStream();
              logger.trace("Opened connection to {}", location);

              final byte[] buffer = new byte[BUFFER];
              final ByteArrayOutputStream baos = new ByteArrayOutputStream();

              while (true) {
                final int read = is.read(buffer);
                if (read == -1) break;

                logger.trace("Read {} bytes from {}", read, location);
                baos.write(buffer, 0, read);
              }

              final byte[] body = baos.toByteArray();
              final byte[] calculatedHash = hasher.digest(body);

              if (!Arrays.equals(hash, calculatedHash)) {
                if (logger.isDebugEnabled()) {
                  logger.debug("Could not validate hash, got {}, but expecting {}",
                      Hasher.bytesToHex(calculatedHash),
                      Hasher.bytesToHex(hash));
                }

                throw new Exception("Could not validate hash!");
              }

              logger.debug("Successfully downloaded and validated hash from location {}", location);
              return body;
            }
          });
    }

    try {
      return executorService.invokeAny(downloads);
    } catch (final InterruptedException e) {
      throw new RuntimeException(e);
    } catch (final ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  public void shutdown() {
    executorService.shutdown();
  }
}
