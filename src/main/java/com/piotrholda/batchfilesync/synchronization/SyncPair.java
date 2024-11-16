package com.piotrholda.batchfilesync.synchronization;

record SyncPair (String source, String target, boolean create, boolean update, boolean delete) {

}
