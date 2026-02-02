package com.scnsoft.eldermark.dump;

import com.scnsoft.eldermark.dump.facade.DumpFacade;
import com.scnsoft.eldermark.dump.model.DumpType;
import com.scnsoft.eldermark.dump.service.DumpFilterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class Main implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Autowired
    private DumpFacade dumpFacade;

    @Autowired
    private DumpFilterFactory dumpFilterFactory;

    @Override
    public void run(String... args) {
        var dumpFilter = dumpFilterFactory.buildFilter();
        dumpFilter.setDumpType(DumpType.HOSPITALIZATIONS);
        dumpFacade.generateDump(dumpFilter);

        dumpFilter.setDumpType(DumpType.ER_VISITS);
        dumpFacade.generateDump(dumpFilter);
    }

    @PostConstruct
    public void init(){
        TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
    }
}
