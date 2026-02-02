package com.scnsoft.eldermark.entity.xds.hl7table;

import javax.persistence.*;

@Entity
@Table(name = HL7DefinedCodeTable.TABLE_NAME)
public abstract class HL7DefinedCodeTable extends HL7CodeTable {

    static final String TABLE_NAME = "HL7UserDefinedCodeTable";

    public HL7DefinedCodeTable() {
    }

    public HL7DefinedCodeTable(String code, String value) {
        super(code, value);
    }
}
