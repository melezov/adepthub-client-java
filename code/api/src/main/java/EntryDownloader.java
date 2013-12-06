import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adepthub.client.Downloader;
import com.adepthub.client.Hasher;
import com.adepthub.client.model.Artifact;
import com.adepthub.client.model.Entry;
import com.adepthub.client.model.json.EntryJsonDeserialization;
import com.eclipsesource.json.JsonObject;

public class EntryDownloader {
  private final Logger logger;
  private final Downloader downloader;
  private final ExecutorService executorService;

  public EntryDownloader(
      final Logger logger,
      final Downloader downloader) {
    this.logger = logger;
    this.downloader = downloader;

    executorService = Executors.newCachedThreadPool();
  }

  public void downloadEntry(final Entry entry) {
    final List<ArtifactDownloader> artifactDownloaders =
        new ArrayList<ArtifactDownloader>();

    for(final Artifact artifact : entry.artifacts) {
      artifactDownloaders.add(new ArtifactDownloader(artifact));
    }

    try {
      executorService.invokeAll(artifactDownloaders);
    } catch (final InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public void shutdown() {
    executorService.shutdown();
  }

  public class ArtifactDownloader implements Callable<byte[]>{
    public final Artifact artifact;

    public ArtifactDownloader(
        final Artifact artifact) {
      this.artifact = artifact;
    }

    @Override
    public byte[] call() throws Exception {
      return downloader.downloadAndVerify(artifact.locations, artifact.hash);
    }
  }

  public static void main(final String[] args) throws Throwable {
    final Logger logger = LoggerFactory.getLogger("adepthub-client-java");
    final Hasher hasher = new Hasher(logger);
    final Downloader downloader = new Downloader(logger, hasher);

    final EntryDownloader entryDownloader =
        new EntryDownloader(logger, downloader);

    final URL url = new URL("http://adepthub.com/adf/ivy");
    final InputStream is = url.openStream();
    final Reader r = new InputStreamReader(is, "UTF-8");
    final JsonObject jo = JsonObject.readFrom(r);
    final Entry entry = EntryJsonDeserialization.INSTANCE.fromJson(jo);

    entryDownloader.downloadEntry(entry);

    entryDownloader.shutdown();
    downloader.shutdown();
  }
}
