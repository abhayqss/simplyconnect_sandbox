package com.scnsoft.eldermark.shared.form;


import com.scnsoft.eldermark.shared.AddressBookSource;

import javax.validation.constraints.NotNull;

public class AddressBookFilter {
    private String secureEmail;

    @NotNull
    private AddressBookSource addressBookSource;

    public String getSecureEmail() {
        return secureEmail;
    }

    public void setSecureEmail(String secureEmail) {
        this.secureEmail = secureEmail;
    }

    public AddressBookSource getAddressBookSource() {
        return addressBookSource;
    }

    public void setAddressBookSource(AddressBookSource addressBookSource) {
        this.addressBookSource = addressBookSource;
    }
}
