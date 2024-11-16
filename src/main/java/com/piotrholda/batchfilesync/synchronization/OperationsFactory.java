package com.piotrholda.batchfilesync.synchronization;

public interface OperationsFactory {

        Operations create(boolean create, boolean update, boolean delete);
}
