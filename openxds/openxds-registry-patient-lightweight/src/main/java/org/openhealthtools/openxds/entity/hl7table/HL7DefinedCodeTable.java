package org.openhealthtools.openxds.entity.hl7table;

import javax.persistence.*;

/**
 * @author sparuchnik
 */
@Entity
@Table(name = HL7DefinedCodeTable.TABLE_NAME)
public abstract class HL7DefinedCodeTable extends HL7CodeTable {

    static final String TABLE_NAME = "HL7DefinedCodeTable";
}
