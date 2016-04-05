package com.dakhniy.filewalker.counter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Sergiy_Dakhniy
 */
public class FileCounter {

    protected Map<String, Integer> fileNames = new ConcurrentHashMap<String, Integer>();
    protected Map<String, Integer> directoryNames = new ConcurrentHashMap<String, Integer>();
    protected AtomicInteger filesCount = new AtomicInteger(0);
    protected AtomicInteger directoriesCount = new AtomicInteger(0);

    public synchronized void add(Map<String, Integer> map, String file) {
        map.compute(file, (fileName, oldVal) -> oldVal == null? 1 : oldVal + 1);
    }

    public Map<String, Integer> getFileNames() {
        return fileNames;
    }

    public Map<String, Integer> getDirectoryNames() {
        return directoryNames;
    }

    public AtomicInteger getFilesCount() {
        return filesCount;
    }

    public AtomicInteger getDirectoriesCount() {
        return directoriesCount;
    }

    public int getDistinctDirectoriesCount() {
        return directoryNames.size();
    }

    public int getDistinctFilesCount() {
        return fileNames.size();
    }

    public List<String> getMostPopularFiles (int top) {
        return getTop(fileNames, top);
    }

    public List<String> getMostPopularDirectories (int top) {
        return getTop(directoryNames, top);
    }

    private List<String> getTop(Map<String, Integer> map, int top) {
        int size = Math.min(map.size(), top);
        List<Map.Entry<String, Integer>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, (o1, o2) -> -1*(o1.getValue()).compareTo(o2.getValue()));
        List<String> topList = new LinkedList<>();
        for(int i=0; i<size;i++) {
            topList.add(list.get(i).getKey());
        }
        return topList;
    }
}
