package com.scnsoft.dto.incoming;

public class PalCareDeviceDto {

    private Long id;

    private String type;

    private String area;

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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    @Override
    public String toString() {
        return "PalCareDeviceDto{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", area='" + area + '\'' +
                '}';
    }
}
