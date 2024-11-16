package com.piotrholda.batchfilesync.synchronization;

import java.nio.file.Path;
import java.util.Optional;

public interface Operation {

    Optional<Path> execute(Path source, Path target);
}
