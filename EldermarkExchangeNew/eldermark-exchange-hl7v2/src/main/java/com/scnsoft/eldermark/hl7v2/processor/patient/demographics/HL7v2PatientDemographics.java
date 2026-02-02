package com.scnsoft.eldermark.hl7v2.processor.patient.demographics;

import com.scnsoft.eldermark.hl7v2.model.AddressDemographics;
import com.scnsoft.eldermark.hl7v2.model.PersonIdentifier;
import com.scnsoft.eldermark.hl7v2.model.PersonName;
import com.scnsoft.eldermark.hl7v2.model.PhoneNumber;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface HL7v2PatientDemographics {

    PersonIdentifier getPatientAccountNumber();

    PersonIdentifier getMothersId();

    PersonName getPatientName();

    PersonName getMotherMaidenName();

    PersonName getPatientAliasName();

    String getRace();

    String getPrimaryLanguage();

    String getMaritalStatus();

    String getReligion();

    String getEthnicGroup();

    String getBirthPlace();

    int getBirthOrder();

    LocalDateTime getDeathDate();

    List<AddressDemographics> getAddressList();

    Gender getSexType();

    LocalDate getBirthDate();

    String getSsn();

    String getRaceIdentifier();

    String getPrimaryLanguageIdentifier();

    String getMaritalStatusIdentifier();

    String getReligionIdentifier();

    String getEthnicGroupIdentifier();

    String getCitizenShip();

    String getVeteranMilitaryStatusIdentifier();

    String getVeteranMilitaryStatus();

    List<PhoneNumber> getPhoneList();

    Boolean getDeathIndicator();

    enum Gender {
        MALE("Male", "M"),
        FEMALE("Female", "F"),
        OTHER("Other", "O"),
        UNKNOWN("Unknown", "U");

        private String value = null;
        private String cdaValue = null;

        Gender(String value, String cdaValue) {
            this.value = value;
            this.cdaValue = cdaValue;
        }

        public static Gender getByString(String sex) {
            if (sex == null) {
                return UNKNOWN;
            } else if (!sex.equalsIgnoreCase("male") && !sex.equalsIgnoreCase("m")) {
                if (!sex.equalsIgnoreCase("female") && !sex.equalsIgnoreCase("f")) {
                    return !sex.equalsIgnoreCase("other") && !sex.equalsIgnoreCase("o") ? UNKNOWN : OTHER;
                } else {
                    return FEMALE;
                }
            } else {
                return MALE;
            }
        }

        public String getValue() {
            return this.value;
        }

        public String getCDAValue() {
            return this.cdaValue;
        }

        public Gender cdaValueOf(String cdaValue) {
            Gender[] types = values();
            Gender[] arr$ = types;
            int len$ = types.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                Gender type = arr$[i$];
                if (type.getCDAValue().equalsIgnoreCase(cdaValue)) {
                    return type;
                }
            }

            return UNKNOWN;
        }
    }
}
