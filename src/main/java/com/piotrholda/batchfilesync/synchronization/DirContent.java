package com.piotrholda.batchfilesync.synchronization;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class DirContent {

    private final Set<DirItem> items = new TreeSet<>();

    public void add(String name, LocalDateTime lastModified, boolean isDirectory) {
        items.add(new DirItem(name, lastModified, isDirectory));
    }

    public Iterator<DirItem> iterator() {
        return items.iterator();
    }
}
