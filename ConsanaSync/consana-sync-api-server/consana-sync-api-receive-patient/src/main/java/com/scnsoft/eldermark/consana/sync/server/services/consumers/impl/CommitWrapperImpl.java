package com.scnsoft.eldermark.consana.sync.server.services.consumers.impl;

import com.scnsoft.eldermark.consana.sync.server.services.consumers.CommitWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Component
public class CommitWrapperImpl implements CommitWrapper {

    private final JdbcTemplate jdbcTemplate;

    private final EntityManager entityManager;

    @Autowired
    public CommitWrapperImpl(JdbcTemplate jdbcTemplate, EntityManager entityManager) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityManager = entityManager;
    }

    @Override
    public <T> T executeWithCommit(Supplier<T> resultSupplier, Consumer<Exception> exceptionHandler) {
        return jdbcTemplate.execute((ConnectionCallback<T>) connection -> {
                    try (connection) {
                        connection.setAutoCommit(false);
                        var result = resultSupplier.get();
                        connection.commit();
                        entityManager.flush();
                        return result;
                    } catch (Exception e) {
                        connection.rollback();
                        exceptionHandler.accept(e);
                        return null;
                    }
                }
        );
    }
}
