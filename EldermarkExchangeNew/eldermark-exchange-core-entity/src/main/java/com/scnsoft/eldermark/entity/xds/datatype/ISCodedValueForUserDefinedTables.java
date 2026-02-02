package com.scnsoft.eldermark.entity.xds.datatype;

import javax.persistence.*;

import com.scnsoft.eldermark.entity.xds.hl7table.HL7UserDefinedCodeTable;

@Entity
@Table(name = "IS_CodedValueForUserDefinedTables")
public class ISCodedValueForUserDefinedTables<TABLE extends HL7UserDefinedCodeTable> extends CodedValueForHL7Table {

    @OneToOne
    @JoinColumn(name = "hl7_user_defined_code_table_id")
    private HL7UserDefinedCodeTable hl7CodeTable;

    public ISCodedValueForUserDefinedTables() {
    }

    public ISCodedValueForUserDefinedTables(String rawCode, HL7UserDefinedCodeTable hl7CodeTable) {
        super(rawCode);
        this.hl7CodeTable = hl7CodeTable;
    }

    public void setHl7TableCode(TABLE hl7TableCode) {
        this.hl7CodeTable = hl7TableCode;
    }

    @Override
    public HL7UserDefinedCodeTable getHl7CodeTable() {
        return  hl7CodeTable;
    }
}
