package com.dakhniy.filewalker.impl;

import com.dakhniy.filewalker.FileWalker;
import com.dakhniy.filewalker.counter.FileCounter;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Sergiy_Dakhniy
 */
public class ConcurrentFileWalker extends FileWalker {

    private ExecutorService executor;
    private List<Future<?>> subTasks = new ArrayList<>();

    public ConcurrentFileWalker(FileCounter counter, ExecutorService executor) {
        super(counter);
        this.executor = executor;
    }


    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if(! dir.toAbsolutePath().equals(rootDir.toAbsolutePath())) {
            Future<?> subTask = executor.submit(() -> walk(dir));
            subTasks.add(subTask);
            return FileVisitResult.SKIP_SUBTREE;
        } else {
            fileCounter.add(fileCounter.getDirectoryNames(), dir.getFileName().toString());
            fileCounter.getDirectoriesCount().incrementAndGet();
            return FileVisitResult.CONTINUE;
        }
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
        return FileVisitResult.TERMINATE;
    }

    @Override
    public FileCounter run(Path rootDir) {
        try {
            this.rootDir = rootDir;
            Future result = executor.submit(()->walk(rootDir));
            subTasks.add(result);
            while (!subTasks.isEmpty()) {
                Future randomTask = subTasks.get(0);
                if(randomTask.isDone()) {
                    subTasks.remove(randomTask);
                }
            }
            executor.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileCounter;
    }
}
