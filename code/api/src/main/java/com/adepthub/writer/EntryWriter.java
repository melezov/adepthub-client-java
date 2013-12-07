package com.adepthub.writer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adepthub.client.model.Artifact;
import com.adepthub.client.model.Entry;
import com.adepthub.writer.pathgenerator.FilePathGenerator;

public class EntryWriter {
  private final Logger logger = LoggerFactory.getLogger(EntryWriter.class.getName());

  private Entry entry;
  private FilePathGenerator generator;

  public EntryWriter() {}

  public EntryWriter(Entry entry, FilePathGenerator generator) {
    this.entry     = entry;
    this.generator = generator;
  }

  public boolean writeArtifacts() {
    boolean hasErrors = false;

    for (Artifact artifact : entry.artifacts) {
      File file = null;
      try {
        file = new File(generator.getFilePath(artifact.hash));
        createParentDirectories(file);
        writeContentToFile(file, artifact.hash);
      } catch (IOException e) {
        hasErrors = true;
        logger.error("Could not write file {}", file.getAbsolutePath());
      }
    }
    return hasErrors;
  }

  private void createParentDirectories(File file) {
    file.mkdirs();
  }

  private void writeContentToFile(File file, byte[] content) throws IOException {
    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
    bos.write(content);
    bos.close();
  }

  public Entry getEntry() {
    return entry;
  }

  public void setEntry(Entry entry) {
    this.entry = entry;
  }

  public FilePathGenerator getGenerator() {
    return generator;
  }

  public void setGenerator(FilePathGenerator generator) {
    this.generator = generator;
  }
}
