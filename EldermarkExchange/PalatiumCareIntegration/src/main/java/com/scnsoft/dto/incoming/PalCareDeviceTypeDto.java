package com.scnsoft.dto.incoming;

public class PalCareDeviceTypeDto {

    private Long id;

    private String type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "PalCareDeviceTypeDto{" +
                "id=" + id +
                ", type='" + type + '\'' +
                '}';
    }
}
