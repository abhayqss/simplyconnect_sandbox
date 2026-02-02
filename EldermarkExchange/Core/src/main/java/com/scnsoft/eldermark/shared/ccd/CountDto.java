package com.scnsoft.eldermark.shared.ccd;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CountDto extends Number {
    @Id
    @Column(name = "count")
    private Long count;

    @Override
    public double doubleValue() {
        return count.doubleValue();
    }

    @Override
    public float floatValue() {
        return count.floatValue();
    }

    @Override
    public int intValue() {
        return count.intValue();
    }

    @Override
    public long longValue() {
        return count;
    }
}
