package com.piotrholda.batchfilesync.synchronization;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;


@ConfigurationProperties
record SyncConfig(List<SyncPair> sync) {
}
