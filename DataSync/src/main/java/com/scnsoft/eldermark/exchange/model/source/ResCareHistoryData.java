package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

import java.util.Date;

@Table(ResCareHistoryData.TABLE_NAME)
public class ResCareHistoryData extends IdentifiableSourceEntity<Long> {
    public static final String TABLE_NAME = "Res_Care_History";
    public static final String UNIQUE_ID = "Unique_ID";

    @Id
    @Column(UNIQUE_ID)
    private long id;

    @Column("Care_Start")
    private Date careStart;

    @Column("Care_End")
    private Date careEnd;

    @Column("Res_Number")
    private Long resNumber;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCareStart() {
        return careStart;
    }

    public void setCareStart(Date careStart) {
        this.careStart = careStart;
    }

    public Date getCareEnd() {
        return careEnd;
    }

    public void setCareEnd(Date careEnd) {
        this.careEnd = careEnd;
    }

    public Long getResNumber() {
        return resNumber;
    }

    public void setResNumber(Long resNumber) {
        this.resNumber = resNumber;
    }
}
