package com.scnsoft.eldermark.entity;

import javax.persistence.*;

@Entity
@NamedNativeQueries({
        @NamedNativeQuery (
                name = "exec__find_merged_patients",
                query = "EXEC dbo.find_merged_patients " +
                        "@ResidentId = :residentId",
                resultClass = MergedResidentIdDto.class
        ),
        @NamedNativeQuery(
                name = "exec__find_patients_and_merged_patients",
                query = "EXEC dbo.find_patients_and_merged_patients " +
                        "@DatabaseId = :databaseId",
                resultClass = MergedResidentIdDto.class
        )
})
public class MergedResidentIdDto extends Number {
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
