package com.dakhniy.filewalker;

import com.dakhniy.filewalker.counter.FileCounter;

import java.io.IOException;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by Sergiy_Dakhniy
 */
public abstract class FileWalker implements FileVisitor<Path>{
    protected FileCounter fileCounter;
    protected Path rootDir;

    public FileWalker(FileCounter fileCounter) {
        this.fileCounter = fileCounter;
    }

    public void setFileCounter(FileCounter fileCounter) {
        this.fileCounter = fileCounter;
    }

    protected FileCounter walk(Path rootDir) {
        try {
            this.rootDir = rootDir;
            Files.walkFileTree(rootDir, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileCounter;
    }

    public FileCounter run(Path rootDir) {
        return walk(rootDir);
    }
}
