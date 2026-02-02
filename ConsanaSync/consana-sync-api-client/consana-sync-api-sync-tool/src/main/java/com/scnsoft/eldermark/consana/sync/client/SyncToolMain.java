package com.scnsoft.eldermark.consana.sync.client;

import com.scnsoft.eldermark.consana.sync.client.beans.ApplicationSyncContext;
import com.scnsoft.eldermark.consana.sync.client.config.SyncToolApplicationConfig;
import com.scnsoft.eldermark.consana.sync.client.services.CommunityService;
import com.scnsoft.eldermark.consana.sync.client.services.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import java.util.stream.Collectors;

@SpringBootApplication
@Import(SyncToolApplicationConfig.class)
public class SyncToolMain implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(SyncToolMain.class, args);
    }

    @Autowired
    private SyncService syncService;

    @Autowired
    private CommunityService communityService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var syncContext = parse(args);

        if (syncContext.getCommunityIds() == null) {
            printHelp();
        } else {
            syncService.process(syncContext);
        }
    }

    public ApplicationSyncContext parse(ApplicationArguments args) {
        var syncContext = new ApplicationSyncContext();
        syncContext.setCheckDataSyncStatus(args.containsOption("cds"));
        syncContext.setCheckDataSyncStatusSyncFirst(args.containsOption("cdssf"));

        if (args.containsOption("i")) {
            syncContext.setCommunityIds(communityService.getInitialSyncEnabledCommunityIds());
        }
        if (args.containsOption("c")) {
            syncContext.setCommunityIds(args.getOptionValues("c").stream().map(Long::valueOf).collect(Collectors.toList()));
        }
        if (args.containsOption("s")) {
            syncContext.setSchedule(Integer.parseInt(args.getOptionValues("s").get(0)));
        }

        return syncContext;
    }

    private void printHelp() {
        System.out.println("--i          : Sync all communities with initial sync flag");
        System.out.println("--c=x,y,z    : Sync communities with specific ids");
        System.out.println("--s=xxx      : Schedule repeated sync. xxx are minutes");
        System.out.println("--cds        : Resync only if last datasync success status updated");
        System.out.println("--cdssf      : If datasync status is tracked, whether to sync on first launch");
        System.out.println("--h          : Print help");
    }
}
