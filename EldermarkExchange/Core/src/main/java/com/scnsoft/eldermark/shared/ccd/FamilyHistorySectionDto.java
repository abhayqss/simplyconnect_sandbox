package com.scnsoft.eldermark.shared.ccd;


import javax.persistence.*;

/**
 * Created by stsiushkevich on 4/30/2015.
 */
@Entity
@Table(name = "FamilyHistorySectionDto")
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "exec__load_ccd_family_history",
                query = "EXEC dbo.load_ccd_family_history " +
                        "@ResidentId = :residentId," +
                        "@SortBy = :sortBy," +
                        "@SortDir = :sortDir," +
                        "@Offset = :offset," +
                        "@Limit = :limit," +
                        "@Aggregated = :aggregated",
                resultClass = FamilyHistorySectionDto.class
        ),
        @NamedNativeQuery (
                name = "exec__load_ccd_family_history_count",
                query = "EXEC dbo.load_ccd_family_history_count " +
                        "@ResidentId = :residentId," +
                        "@Aggregated = :aggregated",
                resultClass = CountDto.class
        )
})
public class FamilyHistorySectionDto extends CcdSectionDto {
    @Column(name = "fh_problem_name")
    private String diagnosis;

    @Column(name = "fh_age_observation_val")
    private String ageAtOnset;

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getAgeAtOnset() {
        return ageAtOnset;
    }

    public void setAgeAtOnset(String ageAtOnset) {
        this.ageAtOnset = ageAtOnset;
    }
}
