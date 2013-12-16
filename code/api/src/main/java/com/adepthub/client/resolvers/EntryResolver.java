package com.adepthub.client.resolvers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;

import org.slf4j.Logger;

import com.adepthub.client.hash.Hasher;
import com.adepthub.client.hash.SHA256;
import com.adepthub.client.model.Entry;
import com.adepthub.client.model.json.EntryJsonDeserialization;
import com.eclipsesource.json.JsonObject;

public class EntryResolver
    extends Paths {

  private final static String ADEPT_SECTION = "metadata";

  public EntryResolver(
      final Logger logger,
      final File userRoot,
      final File tempRoot,
      final Hasher hasher,
      final SHA256 hash) throws IOException {
    super(logger, userRoot, tempRoot, hasher, ADEPT_SECTION, hash);
  }

  public Entry resolve() throws IOException {
    try {
      return readFromCache();
    }
    catch (final IOException e) {
      downloadToCache();
      return readFromCache();
    }
  }

  private Entry deserializeStream(final InputStream is) throws IOException {
    final Reader reader = new InputStreamReader(is, "UTF-8");
    final JsonObject jo = JsonObject.readFrom(reader);
    final Entry entry = EntryJsonDeserialization.INSTANCE.fromJson(jo);
    logger.trace("Deserialized entry: {}", entry);
    return entry;
  }

  public Entry readFromCache() throws IOException {
    final File file = makeUserPath();
    final InputStream is = new FileInputStream(file);
    try {
      final Entry entry = deserializeStream(is);
      logger.debug("Read cached entry: " + entry);
      return entry;
    }
    finally {
      is.close();
    }
  }


  private static final String ADEPTHUB_URL =
      "http://adepthub.com/adf/hash/%s/artifacts.adf";

  private final static int BUFFER_SIZE = 8192;

  public void downloadToCache() throws IOException {
    final URL url = new URL(String.format(ADEPTHUB_URL, hash));
    final InputStream is = url.openStream();
    try {
      final File file = makeTempPath();
      final BufferedInputStream bis = new BufferedInputStream(is);
      final OutputStream os = new FileOutputStream(file);

      try {
        final byte[] buffer = new byte[BUFFER_SIZE];
        while (true) {
          final int read = bis.read(buffer);
          if (read == -1) break;
          os.write(buffer, 0, read);
        }

        logger.debug("Finished downloading entry, moving to cache ...");
        moveToUserPath(file);
      }
      finally {
        os.close();
      }
    }
    finally {
      is.close();
    }
  }
}
