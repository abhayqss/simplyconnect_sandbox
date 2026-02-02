package com.scnsoft.eldermark.entity.community;

import javax.persistence.*;

@Entity
@Table(name = "AutoCloseInterval")
public class AutoCloseInterval {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "value", nullable = false)
    private Long valueInMillis;

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

    public Long getValueInMillis() {
        return valueInMillis;
    }

    public void setValueInMillis(Long valueInMillis) {
        this.valueInMillis = valueInMillis;
    }
}
