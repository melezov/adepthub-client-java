import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adepthub.client.download.DownloadProcess;
import com.adepthub.client.download.Result;
import com.adepthub.client.download.StreamDefinition;
import com.adepthub.client.download.StreamingDownload;
import com.adepthub.client.hash.Hasher;
import com.adepthub.client.hash.SHA256;

public class EntryPointConcurrent {
  public static void main(final String[] args) throws Throwable {
    final Logger logger = LoggerFactory.getLogger("adepthub-client-java");
    final Hasher hasher = new Hasher(logger);
    final ExecutorService executorService =
        Executors.newCachedThreadPool();

    final StreamingDownload streamingDownload =
        new StreamingDownload(logger, hasher);

    final DownloadProcess downloads =
        streamingDownload.download(
            new ArrayList<StreamDefinition>() {{
              add(new StreamDefinition(
                  "http://mirrors.ibiblio.org/maven2/joda-time/joda-time/2.3/joda-time-2.3-javadoc.jar",
                  new File("joda1.jar")));
              add(new StreamDefinition(
                  "http://repo1.maven.org/maven2/joda-time/joda-time/2.3/joda-time-2.3-javadoc.jar",
                  new File("joda2.jar"))); }},
            1301413,
            new SHA256(
                "81d59fea2895ff45419a08cd78216ca4a374aff4cce1ef91ff9d380888cc7ff5"),
            Executors.newCachedThreadPool());

    for (final Result result : downloads.call()) {
      System.out.println(result.elapsedTime);
      System.out.println(result.success);
      System.out.println(result.message);
    }

    executorService.shutdown();
  }
}
