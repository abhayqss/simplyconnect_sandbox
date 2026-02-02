package com.scnsoft.eldermark.entity.xds.hl7table;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;

@Entity
@org.hibernate.annotations.Table(appliesTo = HL7UserDefinedCodeTable.TABLE_NAME, optional = false)
@SecondaryTable(name = HL7UserDefinedCodeTable.TABLE_NAME, pkJoinColumns = @PrimaryKeyJoinColumn(name = "id"))
@DiscriminatorValue("0005")
public class HL7CodeTable0005Race extends HL7UserDefinedCodeTable {

}
