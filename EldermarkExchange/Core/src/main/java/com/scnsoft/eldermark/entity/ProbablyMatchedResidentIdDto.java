package com.scnsoft.eldermark.entity;

import javax.persistence.*;

@Entity
@NamedNativeQueries({
        @NamedNativeQuery (
                name = "exec__find_probably_matched_patients",
                query = "EXEC dbo.find_probably_matched_patients " +
                        "@ResidentId = :residentId",
                resultClass = ProbablyMatchedResidentIdDto.class
        )
})
public class ProbablyMatchedResidentIdDto extends Number {
    @Id
    @Column(name = "resident_id")
    private Long residentId;

    @Override
    public int intValue() {
        return residentId.intValue();
    }

    @Override
    public long longValue() {
        return residentId.longValue();
    }

    @Override
    public float floatValue() {
        return residentId.floatValue();
    }

    @Override
    public double doubleValue() {
        return residentId.doubleValue();
    }
}
