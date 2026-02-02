package com.scnsoft.eldermark.entity.xds.datatype;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable;

@Entity
@Table(name = "CE_CodedElement")
public class CECodedElement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "identifier")
    private String identifier;

    @Column(name = "text")
    private String text;

    @Column(name = "name_of_coding_system")
    private String nameOfCodingSystem;

    @Column(name = "alternate_identifier")
    private String alternateIdentifier;

    @Column(name = "alternate_text")
    private String alternateText;

    @Column(name = "name_of_alternate_coding_system")
    private String nameOfAlternateCodingSystem;

    @OneToOne
    @JoinColumn(name = "hl7_code_table_id")
    private HL7CodeTable hl7CodeTable;
    public CECodedElement() {
    }

    public CECodedElement(String identifier) {
        this.identifier = identifier;
    }

    public CECodedElement(String identifier, String text, String nameOfCodingSystem) {
        this.identifier = identifier;
        this.text = text;
        this.nameOfCodingSystem = nameOfCodingSystem;
    }
    public CECodedElement(String identifier, String text, String nameOfCodingSystem, String alternateIdentifier, String alternateText, String nameOfAlternateCodingSystem) {
        this.identifier = identifier;
        this.text = text;
        this.nameOfCodingSystem = nameOfCodingSystem;
        this.alternateIdentifier = alternateIdentifier;
        this.alternateText = alternateText;
        this.nameOfAlternateCodingSystem = nameOfAlternateCodingSystem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public HL7CodeTable getHl7CodeTable() {
        return hl7CodeTable;
    }

    public void setHl7CodeTable(HL7CodeTable hl7CodeTable) {
        this.hl7CodeTable = hl7CodeTable;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getNameOfCodingSystem() {
        return nameOfCodingSystem;
    }

    public void setNameOfCodingSystem(String nameOfCodingSystem) {
        this.nameOfCodingSystem = nameOfCodingSystem;
    }

    public String getAlternateIdentifier() {
        return alternateIdentifier;
    }

    public void setAlternateIdentifier(String alternateIdentifier) {
        this.alternateIdentifier = alternateIdentifier;
    }

    public String getAlternateText() {
        return alternateText;
    }

    public void setAlternateText(String alternateText) {
        this.alternateText = alternateText;
    }

    public String getNameOfAlternateCodingSystem() {
        return nameOfAlternateCodingSystem;
    }

    public void setNameOfAlternateCodingSystem(String nameOfAlternateCodingSystem) {
        this.nameOfAlternateCodingSystem = nameOfAlternateCodingSystem;
    }

}
