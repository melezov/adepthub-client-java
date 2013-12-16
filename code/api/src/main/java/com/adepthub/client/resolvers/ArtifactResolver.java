package com.adepthub.client.resolvers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;

import com.adepthub.client.download.DownloadProcess;
import com.adepthub.client.download.Result;
import com.adepthub.client.download.StreamDefinition;
import com.adepthub.client.download.StreamingDownload;
import com.adepthub.client.hash.Hasher;
import com.adepthub.client.model.Artifact;

public class ArtifactResolver
    extends Paths {

  private final static String ADEPT_SECTION = "artifacts";

  private final StreamingDownload streamingDownload;
  private final ExecutorService executorService;
  private final Artifact artifact;

  public ArtifactResolver(
      final Logger logger,
      final File userRoot,
      final File tempRoot,
      final Hasher hasher,
      final StreamingDownload streamingDownload,
      final ExecutorService executorService,
      final Artifact artifact) throws IOException {
    super(logger, userRoot, tempRoot, hasher, ADEPT_SECTION, artifact.hash);

    this.streamingDownload = streamingDownload;
    this.executorService = executorService;
    this.artifact = artifact;
  }

  public Callable<Result> resolve() throws IOException {
    return new Callable<Result>() {
      @Override
      public Result call() throws Exception {
        try {
          return readFromCache().call();
        }
        catch (final IOException e) {
          return downloadToCache().call();
        }
      }
    };
  }

  public Callable<Result> readFromCache() throws IOException {
    return new Callable<Result>() {
      @Override
      public Result call() throws Exception {
        final long startedAt = System.currentTimeMillis();

        final File file = makeUserPath();
        if (!hasher.validateFile(file, hash)) {
          throw new IOException("Could not read cached file!");
        }

        return new Result(
            new StreamDefinition(
                artifact,
                "file://" + file,
                file),
            true,
            "Artifact retrieved from cache.",
            System.currentTimeMillis() - startedAt);
      }
    };
  }

  public Callable<Result> downloadToCache() throws IOException {
    final List<StreamDefinition> streams = new ArrayList<StreamDefinition>();

    for (final String location: artifact.locations) {
      streams.add(new StreamDefinition(artifact, location, makeTempPath()));
    }

    final DownloadProcess downloadProcess =
      streamingDownload.download(
          streams,
          artifact.size,
          artifact.hash,
          executorService);

    return new Callable<Result>() {
      @Override
      public Result call() throws Exception {
        final List<Result> results = downloadProcess.call();

        for (final Result result: results) {
          if (result.success) {
            moveToUserPathAndValidateFile(result.streams.output);
            // need to rewrite output filename
            return new Result(
                new StreamDefinition(
                    artifact,
                    result.streams.location,
                    makeUserPath()),
                result.success,
                result.message,
                result.elapsedTime);
          }
        }

        throw new IOException("Could not download artifact: " + artifact);
      }
    };
  }
}
