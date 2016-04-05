import com.dakhniy.filewalker.counter.FileCounter;
import com.dakhniy.filewalker.impl.ConcurrentFileWalker;
import com.dakhniy.filewalker.impl.ForkJoinFileWalker;
import com.dakhniy.filewalker.impl.SingleThreadFileWalker;
import com.dakhniy.filewalker.printer.ResultsPrinter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

/**
 * Created by Sergiy_Dakhniy
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("./");
        SingleThreadFileWalker singleWalker = new SingleThreadFileWalker(new FileCounter());
        ConcurrentFileWalker concurrentWalker = new ConcurrentFileWalker(new FileCounter(), Executors.newFixedThreadPool(20));
        ForkJoinFileWalker forkWalker = new ForkJoinFileWalker(new FileCounter(), new ForkJoinPool());

        ResultsPrinter.printToFile(singleWalker.run(path), path, "single-thread.txt");
        ResultsPrinter.printToFile(concurrentWalker.run(path), path, "multi-thread.txt");
        ResultsPrinter.printToFile(forkWalker.run(path), path, "fork-join.txt");
    }
}
