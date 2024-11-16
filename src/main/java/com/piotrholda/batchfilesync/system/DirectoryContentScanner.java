package com.piotrholda.batchfilesync.system;

import com.piotrholda.batchfilesync.synchronization.ContentScanner;
import com.piotrholda.batchfilesync.synchronization.DirContent;
import com.piotrholda.batchfilesync.synchronization.DirItem;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Iterator;

import static java.nio.file.Files.isDirectory;

@Service
class DirectoryContentScanner implements ContentScanner {

    @Override
    public DirContent scan(String path) {
        Path dir = Paths.get(path);
        DirContent content = new DirContent();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            stream.forEach(p -> content.add(fileName(p), lastModify(p), isDirectory(p)));
            return content;
        } catch (IOException e) {
            throw new UncheckedIOException(String.format("Unable to scan directory %s", path), e);
        }
    }

    private String fileName(Path p) {
        return p.getFileName().toString();
    }

    private LocalDateTime lastModify(Path path) {
        try {
            Instant instant = Files.getLastModifiedTime(path).toInstant();
            return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        } catch (IOException e) {
            throw new UncheckedIOException(String.format("Unable to get last modified time for %s", path), e);
        }
    }
}
