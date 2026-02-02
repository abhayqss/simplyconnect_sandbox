package com.scnsoft.eldermark.entity.event.incident;

import javax.persistence.*;

@Entity
@Table(name = "FreeTextValue")
public class FreeText {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "free_text")
    private String freeText;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFreeText() {
        return freeText;
    }

    public void setFreeText(String freeText) {
        this.freeText = freeText;
    }

    public FreeText() {
        
    }
    
    public FreeText(String freeText) {
        this.freeText = freeText;
    }
}
