package com.scnsoft.eldermark.matcher;

import com.scnsoft.eldermark.matcher.dao.MpiDao;
import no.priv.garshol.duke.Property;
import no.priv.garshol.duke.Record;

import java.util.*;

/**
 * Created by knetkachou on 12/27/2016.
 */
public class ResidentMergerListener extends AbstractResidentMatchListener {

    private final MpiDao dao;

    /**
     * Creates a new listener.
     *
     * @param showmatches Whether to display matches. (On cmd-line: --showmatches)
     * @param showmaybe   Whether to display maybe-matches. --showmaybe
     * @param progress    Whether to display progress reports. --progress
     * @param linkage     True iff in record linkage mode.
     * @param properties
     * @param pretty      Whether to pretty-print records (not compact).
     * @param mpiDao
     */
    public ResidentMergerListener(boolean showmatches, boolean showmaybe, boolean progress, boolean linkage, List<Property> properties, boolean pretty, MpiDao mpiDao) {
        super(showmatches, showmaybe, progress, linkage, properties, pretty);
        this.dao = mpiDao;
    }

    @Override
    public void matches(Record r1, Record r2, double confidence) {
        super.matches(r1, r2, confidence);
        synchronized (dao) {
            dao.insertMergedRecords(r1, r2, false, confidence);
        }
    }

    @Override
    public void matchesPerhaps(Record r1, Record r2, double confidence) {
        super.matchesPerhaps(r1, r2, confidence);
        synchronized (dao) {
            dao.insertMergedRecords(r1, r2, true, confidence);
        }
    }

    @Override
    public void endProcessing() {
        super.endProcessing();
        synchronized (dao) {
            dao.updateMpiLog();
        }
    }

}
