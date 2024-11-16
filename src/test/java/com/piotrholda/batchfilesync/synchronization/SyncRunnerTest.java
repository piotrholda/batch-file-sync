package com.piotrholda.batchfilesync.synchronization;

import ch.qos.logback.core.util.FileUtil;
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

import static org.junit.jupiter.api.Assertions.*;

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
}
