package com.piotrholda.batchfilesync.synchronization;

import com.piotrholda.batchfilesync.Synchronization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Paths;
import java.util.Iterator;

@Service
class SyncRunner implements Synchronization {

    Logger logger = LoggerFactory.getLogger(SyncRunner.class);

    private final SyncConfig syncConfig;
    private final ContentScanner contentScanner;
    private final OperationsFactory operationFactory;

    SyncRunner(SyncConfig syncConfig, ContentScanner contentScanner, OperationsFactory operationFactory) {
        this.syncConfig = syncConfig;
        this.contentScanner = contentScanner;
        this.operationFactory = operationFactory;
    }

    @Override
    public void run() {
        syncConfig.sync().forEach(this::processPair);
    }

    private void processPair(SyncPair syncPair) {
        DirContent sourceContent = contentScanner.scan(syncPair.source());
        DirContent targetContent = contentScanner.scan(syncPair.target());
        Iterator<DirItem> sourceIterator = sourceContent.iterator();
        Iterator<DirItem> targetIterator = targetContent.iterator();
        Operations operations = operationFactory.create(syncPair.create(), syncPair.update(), syncPair.delete());

        DirItem sourceItem = sourceIterator.hasNext() ? sourceIterator.next() : null;
        DirItem targetItem = targetIterator.hasNext() ? targetIterator.next() : null;
        while (sourceItem != null || targetItem != null) {
            if (sourceItem != null && (targetItem == null || sourceItem.compareTo(targetItem) < 0)) {
                logger.info("Copy {} to {}", sourceItemName(syncPair, sourceItem), syncPair.target());
                operations.create().execute(Paths.get(syncPair.source()).resolve(sourceItem.name()), Paths.get(syncPair.target()).resolve(sourceItem.name()));
                sourceItem = sourceIterator.hasNext() ? sourceIterator.next() : null;
            } else if (targetItem != null && (sourceItem == null || targetItem.compareTo(sourceItem) < 0)) {
                logger.info("Item {} exists in the second iterator but not in the first.", targetItemName(syncPair, targetItem));
                targetItem = targetIterator.hasNext() ? targetIterator.next() : null;
            } else {
                logger.info("Comparing items {} and {}", sourceItemName(syncPair, sourceItem), targetItemName(syncPair, targetItem));
                if (sourceItem.isDirectory()) {
                    if (!targetItem.isDirectory()) {
                        logger.info("Item {} is not a directory. ", targetItemName(syncPair, targetItem));
                    } else {
                        logger.info("Item {} is directory. ", sourceItemName(syncPair, sourceItem));
                        processPair(syncPair.subPair(sourceItem.name()));
                    }
                } else if (sourceItem.isNewerThan(targetItem)) {
                    logger.info("Item {} modified {} is newer than {} modified {}", sourceItemName(syncPair, sourceItem), sourceItem.lastModified(), targetItemName(syncPair, targetItem), targetItem.lastModified());
                    operations.update().execute(Paths.get(syncPair.source()).resolve(sourceItem.name()), Paths.get(syncPair.target()).resolve(targetItem.name()));
                }
                sourceItem = sourceIterator.hasNext() ? sourceIterator.next() : null;
                targetItem = targetIterator.hasNext() ? targetIterator.next() : null;
            }
        }
    }

    private static String sourceItemName(SyncPair syncPair, DirItem sourceItem) {
        return syncPair.source() + File.separator + sourceItem.name();
    }

    private static String targetItemName(SyncPair syncPair, DirItem targetItem) {
        return syncPair.target() + File.separator + targetItem.name();
    }
}
