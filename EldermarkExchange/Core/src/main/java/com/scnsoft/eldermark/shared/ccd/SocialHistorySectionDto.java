package com.scnsoft.eldermark.shared.ccd;

import javax.persistence.*;

/**
 * Created by stsiushkevich on 5/5/2015.
 */
@Entity
@Table(name = "SocialHistorySectionDto")
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "exec__load_ccd_social_history",
                query = "EXEC dbo.load_ccd_social_history " +
                        "@ResidentId = :residentId," +
                        "@SortBy = :sortBy," +
                        "@SortDir = :sortDir," +
                        "@Offset = :offset," +
                        "@Limit = :limit," +
                        "@Aggregated = :aggregated",
                resultClass = SocialHistorySectionDto.class
        ),
        @NamedNativeQuery (
                name = "exec__load_ccd_social_history_count",
                query = "EXEC dbo.load_ccd_social_history_count " +
                        "@ResidentId = :residentId," +
                        "@Aggregated = :aggregated",
                resultClass = CountDto.class
        )
})
public class SocialHistorySectionDto extends CcdSectionDto{
    @Column(name="s_history_free_text")
    private String element;

    @Column(name="s_history_observation_value")
    private String description;

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
