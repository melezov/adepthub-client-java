package com.adepthub.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;

import com.adepthub.client.model.Artifact;
import com.adepthub.client.model.Entry;

public class EntryWriter {

  private Entry entry;
  private Logger logger;
  private FilePathGenerator generator;

  public EntryWriter(Entry entry, FilePathGenerator generator) {
    this.entry     = entry;
    this.generator = generator;
  }

  public boolean writeArtifacts() {
    try {
      for (Artifact artifact : entry.artifacts) {
        File file = new File(generator.getFilePath(artifact.hash));
        createParentDirectories(file);
//        writeContentToFile(file, artifact.filename);
      }
      return true;
    } catch (Exception e) {
      logger.error(e.getMessage());
      return false;
    }
  }

  private void createParentDirectories(File file) {
    file.mkdirs();
  }

  private void writeContentToFile(File file, byte[] content) throws IOException {
    BufferedWriter bw = new BufferedWriter(new FileWriter(file));
 //   bw.append(content);
    bw.flush();
    bw.close();
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
