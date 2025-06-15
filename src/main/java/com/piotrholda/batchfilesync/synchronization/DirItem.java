package com.piotrholda.batchfilesync.synchronization;

import java.time.LocalDateTime;

public record DirItem(String name, LocalDateTime lastModified, boolean isDirectory) implements Comparable<DirItem> {

    @Override
    public int compareTo(DirItem o) {
        return name.compareTo(o.name);
    }

    public boolean isNewerThan(DirItem other) {
        // Ignore fractions of seconds (nanos) when comparing
        LocalDateTime thisTruncated = lastModified.withNano(0);
        LocalDateTime otherTruncated = other.lastModified.withNano(0);
        return thisTruncated.isAfter(otherTruncated);
    }
}
