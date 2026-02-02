package org.openhealthtools.openxds.entity.hl7table;

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
@DiscriminatorValue("0465")
public class HL7CodeTable0465NameAddressRepresentation extends HL7DefinedCodeTable {

}