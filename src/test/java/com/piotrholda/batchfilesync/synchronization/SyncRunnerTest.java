package com.piotrholda.batchfilesync.synchronization;

import com.piotrholda.batchfilesync.Synchronization;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest
class SyncRunnerTest {

    @Autowired
    private Synchronization synchronization;

    @MockBean
    private SyncConfig syncConfig;

    @Test
    void shouldCopyFile(@TempDir Path sourceDir, @TempDir Path targetDir) throws IOException {

        // given
        String fileName = "file1.txt";
        Path sourceFile = sourceDir.resolve(fileName);
        String fileContent = "Hello, World!";
        Files.write(sourceFile, fileContent.getBytes());
        Mockito.when(syncConfig.sync()).thenReturn(List.of(new SyncPair(sourceDir.toString(), targetDir.toString(), true, false, false)));

        // when
        synchronization.run();

        // then
        Path targetFile = targetDir.resolve(fileName);
        assertTrue(Files.exists(targetFile));
        assertEquals(fileContent, new String(Files.readAllBytes(targetFile)));
    }

    @Test
    void shouldCopyEmptyDirectory(@TempDir Path sourceDir, @TempDir Path targetDir) throws IOException {

        // given
        String dirName = "subdirectory";
        Path sourceSubdirectory = sourceDir.resolve(dirName);
        Files.createDirectory(sourceSubdirectory);
        Mockito.when(syncConfig.sync()).thenReturn(List.of(new SyncPair(sourceDir.toString(), targetDir.toString(), true, false, false)));

        // when
        synchronization.run();

        // then
        Path targetSubdirectory = targetDir.resolve(dirName);
        assertTrue(Files.exists(targetSubdirectory));
        assertTrue(Files.isDirectory(targetSubdirectory));
    }

    @Test
    void shouldCopyDirectoryWithFile(@TempDir Path sourceDir, @TempDir Path targetDir) throws IOException {

        // given
        String dirName = "subdirectory";
        Path sourceSubdirectory = sourceDir.resolve(dirName);
        Path subdirectory = Files.createDirectory(sourceSubdirectory);

        String fileName = "file1.txt";
        Path sourceFile = subdirectory.resolve(fileName);
        String fileContent = "Hello, World!";
        Files.write(sourceFile, fileContent.getBytes());
        Mockito.when(syncConfig.sync()).thenReturn(List.of(new SyncPair(sourceDir.toString(), targetDir.toString(), true, false, false)));

        // when
        synchronization.run();

        // then
        Path targetSubdirectory = targetDir.resolve(dirName);
        assertTrue(Files.exists(targetSubdirectory));
        assertTrue(Files.isDirectory(targetSubdirectory));

        Path targetFile = targetSubdirectory.resolve(fileName);
        assertTrue(Files.exists(targetFile));
        assertEquals(fileContent, new String(Files.readAllBytes(targetFile)));
    }

    @Test
    void shouldCopyNestedDirectoryWithFile(@TempDir Path sourceDir, @TempDir Path targetDir) throws IOException {

        // given
        String dirName = "subdirectory";
        Path sourceSubdirectory = sourceDir.resolve(dirName);
        Path subdirectory = Files.createDirectory(sourceSubdirectory);

        String secondLevelDirName = "second-subdirectory";
        Path secondLevelSourceSubdirectory = subdirectory.resolve(secondLevelDirName);
        Path secondLevelSubdirectory = Files.createDirectory(secondLevelSourceSubdirectory);

        String fileName = "file1.txt";
        Path sourceFile = secondLevelSubdirectory.resolve(fileName);
        String fileContent = "Hello, World!";
        Files.write(sourceFile, fileContent.getBytes());
        Mockito.when(syncConfig.sync()).thenReturn(List.of(new SyncPair(sourceDir.toString(), targetDir.toString(), true, false, false)));

        // when
        synchronization.run();

        // then
        Path targetSubdirectory = targetDir.resolve(dirName);
        assertTrue(Files.exists(targetSubdirectory));
        assertTrue(Files.isDirectory(targetSubdirectory));

        Path targetSecondLevelSubdirectory = targetSubdirectory.resolve(secondLevelDirName);
        assertTrue(Files.exists(targetSecondLevelSubdirectory));
        assertTrue(Files.isDirectory(targetSecondLevelSubdirectory));

        Path targetFile = targetSecondLevelSubdirectory.resolve(fileName);
        assertTrue(Files.exists(targetFile));
        assertEquals(fileContent, new String(Files.readAllBytes(targetFile)));
    }
}
