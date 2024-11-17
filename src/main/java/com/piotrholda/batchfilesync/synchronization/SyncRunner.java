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
                logger.info("Copy " + syncPair.source() + File.separator + sourceItem.name() + " to " + syncPair.target());
                operations.copy().execute(Paths.get(syncPair.source()).resolve(sourceItem.name()), Paths.get(syncPair.target()).resolve(sourceItem.name()));
                sourceItem = sourceIterator.hasNext() ? sourceIterator.next() : null;
            } else if (targetItem != null && (sourceItem == null || targetItem.compareTo(sourceItem) < 0)) {
                logger.info("Item " + targetItem.name() + " exists in the second iterator but not in the first.");
                targetItem = targetIterator.hasNext() ? targetIterator.next() : null;
            } else {
                if (sourceItem.isDirectory()) {
                    SyncPair subPair = new SyncPair(syncPair.source() + File.separator + sourceItem.name(), syncPair.target() + File.separator + targetItem.name(), syncPair.create(), syncPair.update(), syncPair.delete());
                    logger.info("Item " + sourceItem.name() + " is directory. ");
                    processPair(subPair);
                } else if (sourceItem.isNewerThan(targetItem)) {
                    logger.info("Item " + sourceItem.name() + " has different properties: " + sourceItem.lastModified() + " vs " + targetItem.lastModified());
                }
                sourceItem = sourceIterator.hasNext() ? sourceIterator.next() : null;
                targetItem = targetIterator.hasNext() ? targetIterator.next() : null;
            }
        }
    }
}
