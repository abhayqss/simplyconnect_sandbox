package com.scnsoft.eldermark.dto.serviceplan;

public class ProgramSubTypeDto {
    private Long id;
    private Long programTypeId;
    private String name;
    private String title;
    private String zcode;
    private String zcodeDesc;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProgramTypeId() {
        return programTypeId;
    }

    public void setProgramTypeId(Long programTypeId) {
        this.programTypeId = programTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getZcode() {
        return zcode;
    }

    public void setZcode(String zcode) {
        this.zcode = zcode;
    }

    public String getZcodeDesc() {
        return zcodeDesc;
    }

    public void setZcodeDesc(String zcodeDesc) {
        this.zcodeDesc = zcodeDesc;
    }
}
