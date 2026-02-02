package com.scnsoft.eldermark.entity.password;

import com.scnsoft.eldermark.entity.Employee;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "PasswordHistory")
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(name = "enable_password_history",
                procedureName = "enable_password_history",
                parameters = {
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "databaseId", type = Long.class)
                })
})
public class PasswordHistory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    @ManyToOne
    private Employee employee;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
