package com.dakhniy.filewalker.impl;

import com.dakhniy.filewalker.FileWalker;
import com.dakhniy.filewalker.counter.FileCounter;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Created by Sergiy_Dakhniy
 */
public class SingleThreadFileWalker extends FileWalker {

    public SingleThreadFileWalker(FileCounter fileCounter) {
        super(fileCounter);
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        fileCounter.add(fileCounter.getDirectoryNames(), dir.getFileName().toString());
        fileCounter.getDirectoriesCount().incrementAndGet();
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        fileCounter.add(fileCounter.getFileNames(), file.getFileName().toString());
        fileCounter.getFilesCount().incrementAndGet();
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        fileCounter.add(fileCounter.getFileNames(), file.getFileName().toString());
        fileCounter.getFilesCount().incrementAndGet();
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }
}
