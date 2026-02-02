package com.scnsoft.eldermark.entity.xds.hl7table;

import javax.persistence.*;

/**
 * @author sparuchnik
 */
@Entity
@Table(name = HL7UserDefinedCodeTable.TABLE_NAME)
public abstract class HL7UserDefinedCodeTable extends HL7CodeTable {

    static final String TABLE_NAME = "HL7DefinedCodeTable";

}
