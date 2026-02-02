package com.scnsoft.eldermark.entity;


import javax.persistence.*;

@Entity
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "exec__find_linked_employees",
                query = "EXEC dbo.find_linked_employees " +
                        "@EmployeeId = :employeeId",
                resultClass = LinkedEmployeeIdDto.class
        ),
        @NamedNativeQuery(
                name = "exec__delete_linked_employee",
                query = "EXEC dbo.delete_linked_employee " +
                        "@CurrentEmployeeId = :currentEmployeeId, " +
                        "@LinkedEmployeeIdToRemove  = :employeeId"
        )
})
public class LinkedEmployeeIdDto extends Number {
    @Id
    @Column(name = "employee_id")
    private Long employeeId;

    @Override
    public int intValue() {
        return employeeId.intValue();
    }

    @Override
    public long longValue() {
        return employeeId.longValue();
    }

    @Override
    public float floatValue() {
        return employeeId.floatValue();
    }

    @Override
    public double doubleValue() {
        return employeeId.doubleValue();
    }
}
