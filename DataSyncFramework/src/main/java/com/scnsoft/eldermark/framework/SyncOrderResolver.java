package com.scnsoft.eldermark.framework;

import java.util.List;
import java.util.Set;

public interface SyncOrderResolver {
    List<SyncService> order(Set<SyncService> services);
    List<DatabaseInfo> order(List<DatabaseInfo> databases);
}
