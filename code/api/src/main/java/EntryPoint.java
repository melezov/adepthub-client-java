import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adepthub.client.download.Result;
import com.adepthub.client.download.StreamingDownload;
import com.adepthub.client.hash.Hasher;
import com.adepthub.client.hash.SHA256;
import com.adepthub.client.model.Artifact;
import com.adepthub.client.model.Entry;
import com.adepthub.client.resolvers.ArtifactResolver;
import com.adepthub.client.resolvers.EntryResolver;

public class EntryPoint {
  private static File normalizePath(final Logger logger, final String property) {
    final String retrievedProperty = System.getProperty(property);
    logger.trace("Read \"{}\" property: {}", property, retrievedProperty);

    if (retrievedProperty == null) {
      throw new RuntimeException("Could not retrieve property for \"" + property + "\"");
    }

    try {
      final File canonicalPath = new File(retrievedProperty).getCanonicalFile();
      logger.debug("Resolved path for \"{}\": {}", property, canonicalPath);
      return canonicalPath;
    }
    catch (final IOException e) {
      throw new RuntimeException("Could not initialize the canonical path for: " + retrievedProperty);
    }
  }

  public static void main(final String[] args) throws Throwable {
    final Logger logger = LoggerFactory.getLogger("adepthub-client-java");
    final Hasher hasher = new Hasher(logger);
    final ExecutorService executorService = Executors.newCachedThreadPool();
    final StreamingDownload streamingDownload = new StreamingDownload(logger, hasher);

    final File userRoot = normalizePath(logger, "user.home");
    final File tempRoot = normalizePath(logger, "java.io.tmpdir");

    final SHA256 hash = new SHA256("e346460fe8633aefeb3148098e0773a3ea8e95597e4bb3a8f56cc4ab6e7f0791");
    final EntryResolver entryResolver = new EntryResolver(logger, userRoot, tempRoot, hasher, hash);

    final Entry entry = entryResolver.resolve();

    final List<Callable<Result>> artifactResolvers = new ArrayList<Callable<Result>>();
    for (final Artifact artifact : entry.artifacts) {
      final ArtifactResolver artifactResolver = new ArtifactResolver(logger, userRoot, tempRoot, hasher, streamingDownload, executorService, artifact);
      artifactResolvers.add(artifactResolver.resolve());
    }

    final List<Future<Result>> artifactDownloads = executorService.invokeAll(artifactResolvers);

    final StringBuilder sb = new StringBuilder("trait Dependencies {\n");

    for (final Future<Result> artifactDownload : artifactDownloads) {
      final Result result = artifactDownload.get();
      final Artifact artifact = result.streams.artifact;

      final String org = artifact.metadata.organization[0];
      final String name = artifact.metadata.name[0];
      final String readibleID = name.replaceAll("\\W+", "_");
      final String version = artifact.metadata.version[0];
      final String path = "file://" + result.streams.output;

      sb.append(String.format(
          "  val %s = \"%s\" %% \"%s\" %% \"%s\" at \"%s\"\n",
          readibleID, org, name, version, path));
    }

    sb.append("}\n");

    System.out.println(sb);
  }
}
