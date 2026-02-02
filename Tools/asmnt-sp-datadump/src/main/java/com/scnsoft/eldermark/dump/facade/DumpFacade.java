package com.scnsoft.eldermark.dump.facade;

import com.scnsoft.eldermark.dump.bean.DumpFilter;

public interface DumpFacade {

    void generateDump(DumpFilter dumpFilter);
}
