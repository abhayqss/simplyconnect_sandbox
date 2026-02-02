package com.scnsoft.eldermark.entity.xds.datatype;

import com.scnsoft.eldermark.entity.xds.hl7table.HL7DefinedCodeTable;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ID_CodedValuesForHL7Tables")
public class IDCodedValueForHL7Tables<TABLE extends HL7DefinedCodeTable> extends CodedValueForHL7Table {

    @OneToOne
    @JoinColumn(name = "hl7_defined_code_table_id")
    private HL7DefinedCodeTable hl7CodeTable;


    public IDCodedValueForHL7Tables() {
    }

    public IDCodedValueForHL7Tables(String rawCode, HL7DefinedCodeTable hl7CodeTable) {
        super(rawCode);
        this.hl7CodeTable = hl7CodeTable;
    }

    public void setHl7TableCode(TABLE hl7TableCode) {
        this.hl7CodeTable = hl7TableCode;
    }

    @Override
    public HL7DefinedCodeTable getHl7CodeTable() {
        return hl7CodeTable;
    }
}
