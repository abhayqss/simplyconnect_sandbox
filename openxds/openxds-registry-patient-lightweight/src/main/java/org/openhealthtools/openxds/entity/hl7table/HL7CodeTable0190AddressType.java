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
@DiscriminatorValue("0190")
public class HL7CodeTable0190AddressType extends HL7DefinedCodeTable {

}