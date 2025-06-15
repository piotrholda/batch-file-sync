package com.piotrholda.batchfilesync.system;

import com.piotrholda.batchfilesync.synchronization.DirContent;
import com.piotrholda.batchfilesync.synchronization.DirItem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class DirectoryContentScannerTest {

    @Autowired
    private DirectoryContentScanner scanner;

    @Test
    void shouldScanDirectory(@TempDir Path dir) throws IOException {

        // given
        String fileName = "file1.txt";
        Path file = dir.resolve(fileName);
        Files.createFile(file);
        String subdirectoryName = "subdirectory";
        Path subdirectory = dir.resolve(subdirectoryName);
        Files.createDirectory(subdirectory);

        // when
        DirContent content = scanner.scan(dir.toString());

        // then
        assertNotNull(content);
        Iterator<DirItem> actualIterator = content.iterator();
        Iterator<DirItem> expectedIterator = List.of(
                new DirItem(fileName, LocalDateTime.ofInstant(Files.getLastModifiedTime(file).toInstant(), ZoneId.systemDefault()), false),
                new DirItem(subdirectoryName, LocalDateTime.ofInstant(Files.getLastModifiedTime(subdirectory).toInstant(), ZoneId.systemDefault()), true)
        ).iterator();
        while (actualIterator.hasNext() && expectedIterator.hasNext()) {
            DirItem actualItem = actualIterator.next();
            DirItem expectedItem = expectedIterator.next();
            assertDirItem(actualItem, expectedItem);
        }
        assertTrue(!actualIterator.hasNext() && !expectedIterator.hasNext(), "Iterators have different lengths");
    }

    private void assertDirItem(DirItem actualItem, DirItem expectedItem) {
        assertEquals(expectedItem.name(), actualItem.name());
        // Ignore fractions of seconds when comparing lastModified
        assertEquals(expectedItem.lastModified().withNano(0), actualItem.lastModified().withNano(0));
        assertEquals(expectedItem.isDirectory(), actualItem.isDirectory());
    }
}
