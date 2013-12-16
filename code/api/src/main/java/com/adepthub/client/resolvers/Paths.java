package com.adepthub.client.resolvers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Random;

import org.slf4j.Logger;

import com.adepthub.client.hash.Hasher;
import com.adepthub.client.hash.SHA256;

public abstract class Paths {
  private final static String ADEPT_ROOT = ".adept";

  protected final Logger logger;
  private final File userRoot;
  private final File tempRoot;
  protected final Hasher hasher;
  private final Random random;
  private final String section;
  protected final SHA256 hash;

  public Paths(
      final Logger logger,
      final File userRoot,
      final File tempRoot,
      final Hasher hasher,
      final String section,
      final SHA256 hash) throws IOException {
    this.logger = logger;
    this.userRoot = userRoot;
    this.tempRoot = tempRoot;
    this.hasher = hasher;
    this.section = section;
    this.hash = hash;
    this.random = new Random();
  }

  /* Returns a lowercase hexadecimal representation of requested 16-bit words */
  private String getWords(final int startIndex, final int endIndex) {
    return hash.getHashString().substring(startIndex << 2, endIndex << 2);
  }

  /* Unrolls a 64 character hash into a subdirectory structure */
  private String hashToDirectory() {
    return getWords(0, 1) + File.separator +
           getWords(1, 2) + File.separator +
           getWords(2, 16);
  }

  private String makeRandomFilename() {
    return String.format("%011x-%08x",
        System.currentTimeMillis(),
        random.nextInt());
  }

  private void ensureParentExists(final File parent) throws IOException {
    if (parent.isDirectory()) return;

    logger.debug("Creating non-existing directory: {}", parent);
    if (parent.mkdirs() || parent.isDirectory()) return;

    throw new IOException("Could not create parent directory: " + parent);
  }

  /* returns /tmp/.adept/section/a6f4/5bee/f45c4dbc8e0f2943d02bb34d9d9bb720d4a17d4dc1f0a11cd1ef5858/213412341212343214-123412341234 */
  public File makeTempPath() throws IOException {
    final File parent = new File(
        tempRoot,
        ADEPT_ROOT + File.separator +
        section + File.separator +
        hashToDirectory());

    ensureParentExists(parent);

    final File file = new File(parent, makeRandomFilename());
    //logger.trace("Target file for this {} will be: {}", section, file);
    return file;
  }

  /* returns /users/home/foo/.adept/section/a6f4/5bee/f45c4dbc8e0f2943d02bb34d9d9bb720d4a17d4dc1f0a11cd1ef5858 */
  public File makeUserPath() throws IOException {
    final File file = new File(
        userRoot,
        ADEPT_ROOT + File.separator +
        section + File.separator +
        hashToDirectory());

    final File parent = file.getParentFile();
    ensureParentExists(parent);

    //logger.trace("Target file for this {} will be: {}", section, file);
    return file;
  }

  public void moveToUserPath(final File src) throws IOException {
    final File dst = makeUserPath();
    logger.trace("Moving file {} to {}", src, dst);

    final FileChannel srcChannel = new FileInputStream(src).getChannel();
    try {
      final FileChannel dstChannel = new FileOutputStream(dst).getChannel();
      try {
        dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
        for (long remaining = src.length(); remaining > 0;
          remaining -= srcChannel.transferTo(0, srcChannel.size(), dstChannel));
        dstChannel.close();
      }
      catch (final IOException e){
        dstChannel.close();
        dst.delete();
      }
    }
    finally {
      srcChannel.close();
    }

    src.delete();
  }

  public void moveToUserPathAndValidateFile(final File src) throws IOException {
    final File dst = makeUserPath();
    if (!hasher.validateFile(src, hash)) {
      logger.warn("The source file checksum mismatched, removing source file: {}", src);
      src.delete();
      throw new IOException("File checksum mismatch, deleted source file");
    }

    // we can optimize this via Hasher to do a streaming validation whilst moving
    moveToUserPath(src);

    if (!hasher.validateFile(dst, hash)) {
      dst.delete();
      throw new IOException("File could not be moved properly, cleaning up ...");
    }
  }
}
