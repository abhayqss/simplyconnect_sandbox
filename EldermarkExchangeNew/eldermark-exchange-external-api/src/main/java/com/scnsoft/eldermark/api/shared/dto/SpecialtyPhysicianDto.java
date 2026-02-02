package com.scnsoft.eldermark.api.shared.dto;

import java.util.Objects;

public class SpecialtyPhysicianDto {

    private String name;

    private String role;

    private String phone;

    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }

        if (!(o instanceof SpecialtyPhysicianDto)) {
            return false;
        }

        SpecialtyPhysicianDto sp = (SpecialtyPhysicianDto) o;

        boolean isEqualName = this.name != null && this.name.equals((sp.getName()));
        boolean isEqualRole = this.role != null ? this.role.equals((sp.getRole())) : sp.getRole() == null;
        boolean isEqualAddress = this.address != null ? this.address.equals(sp.getAddress()) : sp.getAddress() == null;
        boolean isEqualPhone = this.phone != null ? this.phone.equals(sp.getPhone()) : sp.getPhone() == null;

        return isEqualName && isEqualRole && isEqualAddress && isEqualPhone;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, role, address, phone);
    }
}
