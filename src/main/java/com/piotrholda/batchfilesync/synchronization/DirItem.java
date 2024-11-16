package com.piotrholda.batchfilesync.synchronization;

import java.time.LocalDateTime;

public record DirItem(String name, LocalDateTime lastModified, boolean isDirectory) implements Comparable<DirItem> {

    @Override
    public int compareTo(DirItem o) {
        return name.compareTo(o.name);
    }

    public boolean isNewerThan(DirItem other) {
        return lastModified.isAfter(other.lastModified);
    }
}
