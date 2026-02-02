package com.scnsoft.eldermark.entity.xds.hl7table;

import javax.persistence.*;

/**
 * This is a base entity for any HL7 table either defined by user, or by HL7.
 *
 * To add codes in database please use <code>addHL7Code</code> procedure as it will also update HL7DefinedCodeTable or
 * HL7UserDefinedCodeTable tables
 *
 * @author sparuchnik
 */
@Entity
@Table(name = HL7CodeTable.TABLE_NAME)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "table_number")
public abstract class HL7CodeTable {

    static final String TABLE_NAME = "HL7CodeTable";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "value", nullable = false)
    private String value;

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
