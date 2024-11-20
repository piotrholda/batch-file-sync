package com.piotrholda.batchfilesync.synchronization.oprtation;

import com.piotrholda.batchfilesync.synchronization.Operation;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

class Update implements Operation {

    @Override
    public Optional<Path> execute(Path source, Path target) {
        return Optional.of(target)
                .map(t -> update(source, t));
    }

    private Path update(Path source, Path target) {
        try {
            return Files.copy(source, target, REPLACE_EXISTING, COPY_ATTRIBUTES);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
