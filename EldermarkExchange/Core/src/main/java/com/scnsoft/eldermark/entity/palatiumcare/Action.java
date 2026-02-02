package com.scnsoft.eldermark.entity.palatiumcare;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name = "NotifyAction")
@Table(name = "PalCare_Action")
public class Action extends BasicEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "max_value")
    private Integer maxValue;

    @Column(name = "min_value")
    private Integer minValue;

    @Column(name = "default_value")
    private Integer defaultValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Integer maxValue) {
        this.maxValue = maxValue;
    }

    public Integer getMinValue() {
        return minValue;
    }

    public void setMinValue(Integer minValue) {
        this.minValue = minValue;
    }

    public Integer getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Integer defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return "Action{" +
                "name='" + name + '\'' +
                ", maxValue=" + maxValue +
                ", minValue=" + minValue +
                ", defaultValue=" + defaultValue +
                '}';
    }
}
