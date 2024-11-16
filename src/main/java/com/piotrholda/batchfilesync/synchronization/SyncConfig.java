package com.piotrholda.batchfilesync.synchronization;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "sync")
record SyncConfig(List<SyncPair> sync) {
}
