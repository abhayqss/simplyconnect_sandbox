package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

import java.util.Date;

@Table(ResPaySourceHistoryData.TABLE_NAME)
public class ResPaySourceHistoryData extends IdentifiableSourceEntity<Long> {
    public static final String TABLE_NAME = "Res_PaySource_History";
    public static final String UNIQUE_ID = "Unique_ID";

    @Id
    @Column(UNIQUE_ID)
    private long id;

    @Column("Res_Number")
    private Long resNumber;

    @Column("Pay_Source")
    private String paySource;

    @Column("Start_Date")
    private Date startDate;

    @Column("End_Date")
    private Date endDate;

    @Column("End_Date_Future")
    private Date endDateFuture;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getResNumber() {
        return resNumber;
    }

    public void setResNumber(Long resNumber) {
        this.resNumber = resNumber;
    }

    public String getPaySource() {
        return paySource;
    }

    public void setPaySource(String paySource) {
        this.paySource = paySource;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDateFuture() {
        return endDateFuture;
    }

    public void setEndDateFuture(Date endDateFuture) {
        this.endDateFuture = endDateFuture;
    }
}
