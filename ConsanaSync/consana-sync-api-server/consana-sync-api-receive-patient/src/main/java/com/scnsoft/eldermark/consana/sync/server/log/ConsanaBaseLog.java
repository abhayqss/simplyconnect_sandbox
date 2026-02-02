package com.scnsoft.eldermark.consana.sync.server.log;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@MappedSuperclass
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class ConsanaBaseLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "process_datetime")
    private Instant processTime;

    @Column(name = "is_success")
    private Boolean isSuccess;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "update_type")
    @Enumerated(EnumType.STRING)
    private ConsanaLogUpdateType updateType;
}
