package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(LivingStatusData.TABLE_NAME)
public class LivingStatusData extends IdentifiableSourceEntity<Long> {
    public static final String TABLE_NAME = "Living_Status";
    public static final String UNIQUE_ID = "unique_id";

    @Id
    @Column(UNIQUE_ID)
    private long id;

    @Column("Description")
    private String description;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
