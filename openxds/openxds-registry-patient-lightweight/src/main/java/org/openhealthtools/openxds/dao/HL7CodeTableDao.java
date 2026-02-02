package org.openhealthtools.openxds.dao;

import org.openhealthtools.openxds.entity.hl7table.HL7CodeTable;

import java.util.List;

public interface HL7CodeTableDao {

    <T extends HL7CodeTable> T findCode(String code, Class<T> tableClass);

    HL7CodeTable findCode(String code, List<Class<? extends HL7CodeTable>> tableClass);
}
