package com.scnsoft.eldermark.shared.palatiumcare.facility;

public class PalCareFacilityOutDto {

    private Long id;

    private String name;

    private String label;

    public PalCareFacilityOutDto(Long id, String name, String label) {
        this.id = id;
        this.name = name;
        this.label = label;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
