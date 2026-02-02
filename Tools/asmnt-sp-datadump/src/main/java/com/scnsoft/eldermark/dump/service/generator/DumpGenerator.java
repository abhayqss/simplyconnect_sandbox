package com.scnsoft.eldermark.dump.service.generator;

import com.scnsoft.eldermark.dump.bean.DumpFilter;
import com.scnsoft.eldermark.dump.model.Dump;
import com.scnsoft.eldermark.dump.model.DumpType;

import java.util.List;

public interface DumpGenerator {

    List<Dump> generateDump(DumpFilter filter);

    DumpType getDumpType();

}
