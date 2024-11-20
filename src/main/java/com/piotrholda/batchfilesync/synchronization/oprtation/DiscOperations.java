package com.piotrholda.batchfilesync.synchronization.oprtation;

import com.piotrholda.batchfilesync.synchronization.Operation;
import com.piotrholda.batchfilesync.synchronization.Operations;

import java.util.Optional;

class DiscOperations implements Operations {

    private final Operation create;
    private final Operation update;

    DiscOperations(boolean create, boolean update, boolean delete) {
        this.create = create ? new Create() : null;
        this.update = update ? new Update() : null;
    }

    @Override
    public Operation create() {
        return Optional.ofNullable(create).orElse((source, target) -> Optional.empty());
    }

    @Override
    public Operation update() {
        return Optional.ofNullable(update).orElse((source, target) -> Optional.empty());
    }
}
