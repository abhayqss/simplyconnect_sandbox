package org.openhealthtools.openxds.entity.hl7table;

import javax.persistence.*;

/**
 * @author sparuchnik
 */
@Entity
@org.hibernate.annotations.Table(appliesTo = HL7UserDefinedCodeTable.TABLE_NAME, optional = false)
@SecondaryTable(name = HL7UserDefinedCodeTable.TABLE_NAME, pkJoinColumns = @PrimaryKeyJoinColumn(name = "id"))
@DiscriminatorValue("0001")
public class HL7CodeTable0001AdministrativeSex extends HL7UserDefinedCodeTable {

}
