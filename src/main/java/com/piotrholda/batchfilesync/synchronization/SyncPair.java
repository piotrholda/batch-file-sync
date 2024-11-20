package com.piotrholda.batchfilesync.synchronization;

import java.io.File;

record SyncPair (String source, String target, boolean create, boolean update, boolean delete) {
    SyncPair subPair(String subDirectoryName) {
        return new SyncPair(source() + File.separator + subDirectoryName, target() + File.separator + subDirectoryName, create(), update(), delete());
    }
}
