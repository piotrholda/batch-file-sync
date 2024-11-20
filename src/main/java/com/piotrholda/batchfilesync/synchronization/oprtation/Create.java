package com.piotrholda.batchfilesync.synchronization.oprtation;

import com.piotrholda.batchfilesync.synchronization.Operation;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;

import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;

class Create implements Operation {

    @Override
    public Optional<Path> execute(Path source, Path target) {
        return Optional.of(target)
                .filter(Files::notExists)
                .map(t -> copy(source, t));
    }

    private Path copy(Path source, Path target) {
        try {
            if (Files.isDirectory(source)) {
                copyDirectoryRecursively(source, target);
                return target;
            } else {
                return Files.copy(source, target, COPY_ATTRIBUTES);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void copyDirectoryRecursively(Path sourceDir, Path targetDir) throws IOException {
        Files.walkFileTree(sourceDir, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path targetPath = targetDir.resolve(sourceDir.relativize(dir));
                if (!Files.exists(targetPath)) {
                    Files.createDirectories(targetPath); // Create target directory
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path targetPath = targetDir.resolve(sourceDir.relativize(file));
                Files.copy(file, targetPath, StandardCopyOption.REPLACE_EXISTING); // Copy file
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
