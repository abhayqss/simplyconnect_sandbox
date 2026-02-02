package com.scnsoft.eldermark.entity.xds.hl7table;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;

/**
 * @author sparuchnik
 */
@Entity
@org.hibernate.annotations.Table(appliesTo = HL7DefinedCodeTable.TABLE_NAME, optional = false)
@SecondaryTable(name = HL7DefinedCodeTable.TABLE_NAME, pkJoinColumns = @PrimaryKeyJoinColumn(name = "id"))
@DiscriminatorValue("0136")
public class HL7CodeTable0136YesNoIndicator extends HL7DefinedCodeTable {

}
