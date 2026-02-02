package com.scnsoft.eldermark.framework;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SyncOrderResolverImpl implements SyncOrderResolver {
    @Override
    public List<SyncService> order(Set<SyncService> services) {
        List<SyncService> orderedServices = new ArrayList<SyncService>();
        Set<Class<? extends SyncService>> servicesClassesSet = new HashSet<Class<? extends SyncService>>();

        List<SyncService> unprocessedServices = new ArrayList<SyncService>(services);
        List<SyncService> servicesProcessedByIteration;
        do {
            servicesProcessedByIteration = new ArrayList<SyncService>();
            for (SyncService syncService : unprocessedServices) {
                List<Class<? extends SyncService>> dependencies = syncService.dependsOn();
                if (dependencies == null || dependencies.isEmpty() || servicesClassesSet.containsAll(dependencies)) {
                    servicesProcessedByIteration.add(syncService);
                    orderedServices.add(syncService);
                    servicesClassesSet.add(syncService.getClass());
                }
            }
            unprocessedServices.removeAll(servicesProcessedByIteration);
        } while (unprocessedServices.size() > 0 && servicesProcessedByIteration.size() > 0);

        if (unprocessedServices.size() > 0) {
            throw new RuntimeException("Failed to resolve dependencies for the following services: "
                    + unprocessedServices.toString());
        }

        return orderedServices;
    }

    public List<DatabaseInfo> order(List<DatabaseInfo> databases) {
        List<DatabaseInfo> sortedByLastSyncDateAsc = new ArrayList<DatabaseInfo>(databases);

        Collections.sort(sortedByLastSyncDateAsc, new Comparator<DatabaseInfo>() {
            @Override
            public int compare(DatabaseInfo o1, DatabaseInfo o2) {
                Date d1 = o1.getLastSyncDate();
                Date d2 = o2.getLastSyncDate();
                if (d1 != null && d2 != null) return d1.compareTo(d2);
                else if (d1 == null && d2 != null) return -1;
                else if (d1 != null) return 1;
                else return 0;
            }
        });

        return sortedByLastSyncDateAsc;
    }
}
