package com.scnsoft.eldermark.consana.sync.server.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hl7.fhir.instance.model.Coding;

import javax.persistence.*;

@Entity
@Table(name = "ConsanaUnknownCode")
@Data
@NoArgsConstructor
public class ConsanaUnknownCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "system")
    private String system;

    @Column(name = "display")
    private String display;

    @Column(name = "source")
    private String source;

    public ConsanaUnknownCode(Coding coding, String source) {
        this.code = coding.getCode();
        this.system = coding.getSystem();
        this.display = coding.getDisplay();
        this.source = source;
    }
}
