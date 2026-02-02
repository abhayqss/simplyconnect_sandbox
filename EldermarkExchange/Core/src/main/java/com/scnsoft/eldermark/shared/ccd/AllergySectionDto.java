package com.scnsoft.eldermark.shared.ccd;

import javax.persistence.*;

@Entity
@Table(name = "AllergySectionDto")
@NamedNativeQueries({
    @NamedNativeQuery (
            name = "exec__load_ccd_allergies",
            query = "EXEC dbo.load_ccd_allergies " +
                    "@ResidentId = :residentId," +
                    "@SortBy = :sortBy," +
                    "@SortDir = :sortDir," +
                    "@Offset = :offset," +
                    "@Limit = :limit," +
                    "@Aggregated = :aggregated",
            resultClass = AllergySectionDto.class
    ),
    @NamedNativeQuery (
            name = "exec__load_ccd_allergies_count",
            query = "EXEC dbo.load_ccd_allergies_count " +
                    "@ResidentId = :residentId," +
                    "@Aggregated = :aggregated",
            resultClass = CountDto.class
    )
})
public class AllergySectionDto extends CcdSectionDto {
    @Column(name = "observation_status_code_text")
    private String status;

    @Column(name = "observation_product_text")
    private String product;

    @Column(name = "observation_reactions")
    private String reactions;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getReactions() {
        return reactions;
    }

    public void setReactions(String reactions) {
        this.reactions = reactions;
    }
}
