package com.scnsoft.eldermark.shared.ccd;

import javax.persistence.*;

/**
 * Created by stsiushkevich on 4/29/2015.
 */
@Entity
@Table(name = "ResultSectionDto")
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "exec__load_ccd_advance_directives",
                query = "EXEC dbo.load_ccd_advance_directives " +
                        "@ResidentId = :residentId," +
                        "@SortBy = :sortBy," +
                        "@SortDir = :sortDir," +
                        "@Offset = :offset," +
                        "@Limit = :limit," +
                        "@Aggregated = :aggregated",
                resultClass = AdvanceDirectiveSectionDto.class
        ),
        @NamedNativeQuery (
                name = "exec__load_ccd_advance_directives_count",
                query = "EXEC dbo.load_ccd_advance_directives_count " +
                        "@ResidentId = :residentId," +
                        "@Aggregated = :aggregated",
                resultClass = CountDto.class
        )
})
public class AdvanceDirectiveSectionDto extends CcdSectionDto{
    @Column(name = "directive_type")
    private String type;

    @Column(name = "directive_verification")
    private String verification;

    @Column(name = "directive_supporting_documents")
    private String supportingDocuments;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVerification() {
        return verification;
    }

    public void setVerification(String verification) {
        this.verification = verification;
    }

    public String getSupportingDocuments() {
        return supportingDocuments;
    }

    public void setSupportingDocuments(String supportingDocuments) {
        this.supportingDocuments = supportingDocuments;
    }
}
