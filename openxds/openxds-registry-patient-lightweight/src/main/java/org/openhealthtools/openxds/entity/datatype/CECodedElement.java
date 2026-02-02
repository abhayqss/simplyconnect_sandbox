package org.openhealthtools.openxds.entity.datatype;

import org.openhealthtools.openxds.entity.hl7table.HL7CodeTable;

import javax.persistence.*;

@Entity
@Table(name = "CE_CodedElement")
public class CECodedElement {
    private static final long serialVersionUID = 1L;

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
    private String nameOfalternateCodingSystem;

    @OneToOne
    @JoinColumn(name = "hl7_code_table_id")
    private HL7CodeTable hl7CodeTable;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
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

    public String getNameOfalternateCodingSystem() {
        return nameOfalternateCodingSystem;
    }

    public void setNameOfalternateCodingSystem(String nameOfalternateCodingSystem) {
        this.nameOfalternateCodingSystem = nameOfalternateCodingSystem;
    }

    public HL7CodeTable getHl7CodeTable() {
        return hl7CodeTable;
    }

    public void setHl7CodeTable(HL7CodeTable hl7CodeTable) {
        this.hl7CodeTable = hl7CodeTable;
    }
}
