package com.dakhniy.filewalker.printer;

import com.dakhniy.filewalker.counter.FileCounter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by Sergiy_Dakhniy
 */
public class ResultsPrinter {
    public static void printToConsole(FileCounter counter, Path root) {
        System.out.println(formatOutput(counter, root));
    }
    public static void printToFile(FileCounter counter, Path root, String fileName) {
        File file = new File(fileName);
        if(file.exists()) {
            if(!file.delete()){
                throw new RuntimeException("Cannot delete file");
            }
        }
        try {
            if(!file.createNewFile()){
                throw new RuntimeException("Cannot create file");
            }else {
                FileUtils.writeStringToFile(file, formatOutput(counter, root), false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String formatOutput(FileCounter counter, Path root) {
        StringBuilder builder = new StringBuilder(String.format("Results for directory %s%n", root.toAbsolutePath().toString()))
            .append(String.format("Total files: %s%n", counter.getFilesCount()))
            .append(String.format("Distinct file names: %s%n", counter.getDistinctFilesCount()))
            .append(String.format("Total directories: %s%n", counter.getDirectoriesCount()))
            .append(String.format("Distinct directory names: %s%n", counter.getDistinctDirectoriesCount()))
            .append(String.format("Top 10 file names:%n"));
        for (String fileName: counter.getMostPopularFiles(10)) {
            builder.append(String.format("- %s%n",fileName));
        }
        builder.append(String.format("Top 10 directory names:%n"));
        for (String dirName: counter.getMostPopularDirectories(10)) {
            builder.append(String.format("- %s%n",dirName));
        }
        return builder.toString();
    }
}
