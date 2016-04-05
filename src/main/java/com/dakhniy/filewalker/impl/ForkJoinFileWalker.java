package com.dakhniy.filewalker.impl;

import com.dakhniy.filewalker.FileWalker;
import com.dakhniy.filewalker.counter.FileCounter;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Created by Sergiy_Dakhniy
 */
public class ForkJoinFileWalker extends FileWalker {

    private ForkJoinPool pool;
    private CounterRecursiveAction currentAction;

    public ForkJoinFileWalker(FileCounter counter, ForkJoinPool pool) {
        super(counter);
        this.pool = pool;
    }


    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if(! dir.toAbsolutePath().equals(rootDir.toAbsolutePath())) {
            CounterRecursiveAction action = new CounterRecursiveAction(dir);
            action.fork();
            currentAction.subActions.add(action);
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

    class CounterRecursiveAction extends RecursiveAction {

        private Path rootDir;
        private List<RecursiveAction> subActions = new ArrayList<>();

        public CounterRecursiveAction(Path rootDir) {
            this.rootDir = rootDir;
        }

        @Override
        protected void compute() {
            ForkJoinFileWalker walker = new ForkJoinFileWalker(fileCounter, pool);
            walker.currentAction = this;
            walker.walk(rootDir);
            subActions.forEach(RecursiveAction::join);
        }
    }

    @Override
    public FileCounter run(Path rootDir){
        currentAction = new CounterRecursiveAction(rootDir);
        pool.invoke(currentAction);
        return fileCounter;
    }
}
