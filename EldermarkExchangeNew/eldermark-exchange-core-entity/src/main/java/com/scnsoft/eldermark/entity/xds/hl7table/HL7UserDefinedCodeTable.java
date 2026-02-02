package com.scnsoft.eldermark.entity.xds.hl7table;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = HL7UserDefinedCodeTable.TABLE_NAME)
public abstract class HL7UserDefinedCodeTable extends HL7CodeTable {

    static final String TABLE_NAME = "HL7DefinedCodeTable";

    public HL7UserDefinedCodeTable() {
    }

    public HL7UserDefinedCodeTable(String code, String value) {
        super(code, value);
    }
}
