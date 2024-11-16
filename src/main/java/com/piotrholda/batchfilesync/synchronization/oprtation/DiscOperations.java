package com.piotrholda.batchfilesync.synchronization.oprtation;

import com.piotrholda.batchfilesync.synchronization.Operation;
import com.piotrholda.batchfilesync.synchronization.Operations;

import java.util.Optional;

class DiscOperations implements Operations {

    private final Optional<Operation> copy;

    DiscOperations(boolean create, boolean update, boolean delete) {
        this.copy = create? Optional.of(new Copy()) : Optional.empty();
    }

    @Override
    public Operation copy() {return copy.orElse((source, target) -> Optional.empty());}
}
