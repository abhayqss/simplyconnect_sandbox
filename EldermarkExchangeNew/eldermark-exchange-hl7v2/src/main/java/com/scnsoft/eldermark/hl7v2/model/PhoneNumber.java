package com.scnsoft.eldermark.hl7v2.model;

public class PhoneNumber {
    private PhoneType type = null;
    private String countryCode = null;
    private String areaCode = null;
    private String number = null;
    private String extension = null;
    private String note = null;
    private String email = null;

    public PhoneNumber() {
    }

    public PhoneNumber(PhoneType type) {
        this.type = type;
    }

    public PhoneNumber(PhoneType type, String areaCode, String number) {
        this.type = type;
        this.areaCode = areaCode;
        this.number = number;
    }

    public String getAreaCode() {
        return this.areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getExtension() {
        return this.extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public PhoneType getType() {
        return this.type;
    }

    public void setType(PhoneType type) {
        this.type = type;
    }

    public boolean isEmpty() {
        return this.number == null && this.extension == null;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public enum PhoneType {
        HOME("HOME", "Home", "HP"),
        WORK("WORK", "Work", "WP"),
        CELL("CELL", "Cell", "MC"),
        EMERGENCY("EMERGENCY", "Emergency", "EC"),
        FAX("FAX", "Fax", "FAX"), //originally CDA value was UN
        SERVICE("SERVICE", "Service", "UN"),
        UNKNOWN("UNKNOWN", "Unknown", "UN");

        private String value = null;
        private String printValue = null;
        private String cdaValue = null;

        PhoneType(String value, String printValue, String cdaValue) {
            this.value = value;
            this.printValue = printValue;
            this.cdaValue = cdaValue;
        }

        public String getValue() {
            return this.value;
        }

        public String getPrintValue() {
            return this.printValue;
        }

        public String getCDAValue() {
            return this.cdaValue;
        }

        public PhoneType cdaValueOf(String cdaValue) {
            PhoneType[] types = values();
            PhoneType[] arr$ = types;
            int len$ = types.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                PhoneType type = arr$[i$];
                if (type.getCDAValue().equalsIgnoreCase(cdaValue)) {
                    return type;
                }
            }

            return UNKNOWN;
        }

        public static PhoneType getByString(String phoneNumber) {
            if (phoneNumber == null) {
                return UNKNOWN;
            } else if (phoneNumber.equalsIgnoreCase("home")) {
                return HOME;
            } else if (phoneNumber.equalsIgnoreCase("work")) {
                return WORK;
            } else if (phoneNumber.equalsIgnoreCase("cell")) {
                return CELL;
            } else if (phoneNumber.equalsIgnoreCase("Emergency")) {
                return EMERGENCY;
            } else if (phoneNumber.equalsIgnoreCase("Fax")) {
                return FAX;
            } else {
                return phoneNumber.equalsIgnoreCase("Service") ? SERVICE : UNKNOWN;
            }
        }
    }
}
