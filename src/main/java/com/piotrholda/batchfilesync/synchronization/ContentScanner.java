package com.piotrholda.batchfilesync.synchronization;

import java.util.Iterator;

public interface ContentScanner {
   DirContent scan(String path);
}
