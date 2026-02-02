package com.scnsoft.eldermark.entity.xds.datatype;

import javax.persistence.*;

import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable;

@MappedSuperclass
public abstract class CodedValueForHL7Table {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "raw_code", nullable = false)
    private String rawCode;

    public CodedValueForHL7Table() {
    }

    public CodedValueForHL7Table(String rawCode) {
        this.rawCode = rawCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRawCode() {
        return rawCode;
    }

    public void setRawCode(String rawCode) {
        this.rawCode = rawCode;
    }

    public abstract HL7CodeTable getHl7CodeTable();
}
