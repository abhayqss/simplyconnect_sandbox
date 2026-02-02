package com.scnsoft.eldermark.shared.ccd;

import com.scnsoft.eldermark.shared.json.CustomDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "MedicationSectionDto")
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "exec__load_ccd_medications",
                query = "EXEC dbo.load_ccd_medications " +
                        "@ResidentId = :residentId," +
                        "@SortBy = :sortBy," +
                        "@SortDir = :sortDir," +
                        "@Offset = :offset," +
                        "@Limit = :limit," +
                        "@Aggregated = :aggregated",
                resultClass = MedicationSectionDto.class
        ),
        @NamedNativeQuery (
                name = "exec__load_ccd_medications_count",
                query = "EXEC dbo.load_ccd_medications_count " +
                        "@ResidentId = :residentId," +
                        "@Aggregated = :aggregated",
                resultClass = CountDto.class
        )
})
public class MedicationSectionDto extends CcdSectionDto {
    @Column(name = "mdc_started")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date startDate;

    @Column(name = "mdc_stopped")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date endDate;

    @Column(name="mdc_free_text_sig")
    private String direction;

    @Column(name="mdc_status_code")
    private String status;

    @Column(name="mdc_info_product_name_text")
    private String productNameText;

    @Column(name="indications")
    private String indications;

    @Column(name="instr_text")
    private String instruction;

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

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProductNameText() {
        return productNameText;
    }

    public void setProductNameText(String productNameText) {
        this.productNameText = productNameText;
    }

    public String getIndications() {
        return indications;
    }

    public void setIndications(String indications) {
        this.indications = indications;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }
}
