package com.scnsoft.eldermark.entity.serviceplan;

import javax.persistence.*;

@Entity
@Table(name = "ProgramSubType")
public class ProgramSubType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @JoinColumn(name = "zcode_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private ZCode zCode;

    @JoinColumn(name = "program_type_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private ProgramType programType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public ZCode getzCode() {
        return zCode;
    }

    public void setzCode(ZCode zCode) {
        this.zCode = zCode;
    }

    public ProgramType getProgramType() {
        return programType;
    }

    public void setProgramType(ProgramType programType) {
        this.programType = programType;
    }
}
