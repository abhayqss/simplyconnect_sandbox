package com.scnsoft.eldermark.dump.service.writer;

import com.scnsoft.eldermark.dump.model.Dump;
import com.scnsoft.eldermark.dump.model.DumpType;

public interface DumpWriter<D extends Dump> {

    void writeDump(D dump);

    DumpType getDumpType();

}
