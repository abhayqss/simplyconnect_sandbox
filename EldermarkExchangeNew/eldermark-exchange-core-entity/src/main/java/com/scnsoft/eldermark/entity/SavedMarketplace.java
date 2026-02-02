package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "SavedMarketplace")
public class SavedMarketplace {

    @EmbeddedId
    private SavedMarketplace.Id id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    private Employee employee;

    @Column(name = "employee_id", nullable = false, insertable = false, updatable = false)
    private Long employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marketplace_id", insertable = false, updatable = false)
    private Marketplace marketplace;

    @Column(name = "marketplace_id", nullable = false, insertable = false, updatable = false)
    private Long marketplaceId;

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Marketplace getMarketplace() {
        return marketplace;
    }

    public void setMarketplace(Marketplace marketplace) {
        this.marketplace = marketplace;
    }

    public Long getMarketplaceId() {
        return marketplaceId;
    }

    public void setMarketplaceId(Long marketplaceId) {
        this.marketplaceId = marketplaceId;
    }

    @Embeddable
    public static class Id implements Serializable {

        @Column(name = "employee_id", nullable = false, insertable = false, updatable = false)
        private Long employeeId;

        @Column(name = "marketplace_id", nullable = false, insertable = false, updatable = false)
        private Long marketplaceId;

        public Long getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(Long employeeId) {
            this.employeeId = employeeId;
        }

        public Long getMarketplaceId() {
            return marketplaceId;
        }

        public void setMarketplaceId(Long marketplaceId) {
            this.marketplaceId = marketplaceId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SavedMarketplace.Id id = (SavedMarketplace.Id) o;
            return Objects.equals(employeeId, id.employeeId) &&
                    Objects.equals(marketplaceId, id.marketplaceId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(employeeId, marketplaceId);
        }
    }
}
