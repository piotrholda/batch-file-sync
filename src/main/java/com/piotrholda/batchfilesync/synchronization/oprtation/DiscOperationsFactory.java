package com.piotrholda.batchfilesync.synchronization.oprtation;

import com.piotrholda.batchfilesync.synchronization.OperationsFactory;
import org.springframework.stereotype.Component;

@Component
class DiscOperationsFactory implements OperationsFactory {

    @Override
    public DiscOperations create(boolean create, boolean update, boolean delete) {
        return new DiscOperations(create, update, delete);
    }
}
