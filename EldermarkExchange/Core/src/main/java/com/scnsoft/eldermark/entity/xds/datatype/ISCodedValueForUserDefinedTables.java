package com.scnsoft.eldermark.entity.xds.datatype;


import com.scnsoft.eldermark.entity.xds.hl7table.HL7UserDefinedCodeTable;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "IS_CodedValueForUserDefinedTables")
public class ISCodedValueForUserDefinedTables<TABLE extends HL7UserDefinedCodeTable> extends CodedValueForHL7Table {

    @OneToOne
    @JoinColumn(name = "hl7_user_defined_code_table_id")
    private HL7UserDefinedCodeTable hl7CodeTable;


    public void setHl7TableCode(TABLE hl7TableCode) {
        this.hl7CodeTable = hl7TableCode;
    }

    @Override
    public HL7UserDefinedCodeTable getHl7CodeTable() {
        return  hl7CodeTable;
    }
}
