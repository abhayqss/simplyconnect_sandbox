package com.scnsoft.eldermark.matcher;

import com.scnsoft.eldermark.matcher.dao.MpiDao;
import no.priv.garshol.duke.ConfigLoader;
import no.priv.garshol.duke.Configuration;
import no.priv.garshol.duke.Processor;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by knetkachou on 1/26/2017.
 */
@DisallowConcurrentExecution
public class ResidentMergerJob implements Job {

    private static final String DUKE_CONFIG_FILE = "classpath:duke-merger.xml";
    private static final String DUKE_FIRST_TIME_CONFIG_FILE = "classpath:duke-first-time-merger.xml";

    public void execute(JobExecutionContext context)
            throws JobExecutionException {
        System.out.println();
        MpiDao mpiDao = new MpiDao();
        boolean firstTimeMerge = mpiDao.isFirstTimeMerge();
        long startTime = System.currentTimeMillis();
        Configuration config;
        try {
            config = ConfigLoader.load(firstTimeMerge ? DUKE_FIRST_TIME_CONFIG_FILE : DUKE_CONFIG_FILE);
            System.out.println("Loaded config, firstTimeMerge=" + firstTimeMerge);
        } catch (Exception e) {
            System.out.println("Error during config load");
            e.printStackTrace();
            return;
        }

        Processor proc = new Processor(config);
        proc.setThreads(2);
        proc.addMatchListener(new ResidentMergerListener(true, true, true, false,
                config.getProperties(),
                true, mpiDao));
        if (firstTimeMerge) {
            proc.deduplicate();
        } else {
            proc.link();
        }

        proc.close();

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("ResidentMergerJob finished work in " + elapsedTime / 1000 + "s");

    }
}
