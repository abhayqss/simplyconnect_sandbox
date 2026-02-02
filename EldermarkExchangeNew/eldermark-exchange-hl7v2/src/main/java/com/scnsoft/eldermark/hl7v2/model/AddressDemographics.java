package com.scnsoft.eldermark.hl7v2.model;

public class AddressDemographics {

    private AddressType addType;
    private String addLine1;
    private String addLine2;
    private String addCity;
    private String addState;
    private String addCounty;
    private String addCountry;
    private String addZip;

    public AddressDemographics() {
    }

    public AddressType getAddType() {
        return this.addType;
    }

    public void setAddType(AddressType addType) {
        this.addType = addType;
    }

    public String getAddLine1() {
        return this.addLine1;
    }

    public void setAddLine1(String addLine1) {
        this.addLine1 = addLine1;
    }

    public String getAddLine2() {
        return this.addLine2;
    }

    public void setAddLine2(String addLine2) {
        this.addLine2 = addLine2;
    }

    public String getAddCity() {
        return this.addCity;
    }

    public void setAddCity(String addCity) {
        this.addCity = addCity;
    }

    public String getAddState() {
        return this.addState;
    }

    public void setAddState(String addState) {
        this.addState = addState;
    }

    public String getAddCountry() {
        return this.addCountry;
    }

    public void setAddCountry(String addCountry) {
        this.addCountry = addCountry;
    }

    public String getAddCounty() {
        return this.addCounty;
    }

    public void setAddCounty(String addCounty) {
        this.addCounty = addCounty;
    }

    public String getAddZip() {
        return this.addZip;
    }

    public void setAddZip(String addZip) {
        this.addZip = addZip;
    }

    public boolean isEmpty() {
        return this.addLine1 == null && this.addLine2 == null && this.addCity == null && this.addState == null && this.addZip == null;
    }

    public enum AddressType {
        HOME("HOME", "Home", "H"),
        WORK("WORK", "Work", "O"),
        OTHER("OTHER", "Other", null),
        UNKNOWN("UNKNOWN", "Unknown", null),
        TEMPORARY("TEMPORARY", "Temporary", "C"),
        PERMANENT("PERMANENT", "Permanent", "P"),
        MAILING("MAILING", "Mailing", "M"),
        BUSINESS("BUSINESS", "Business", "B"),
        BIRTH("BIRTH", "Birth", "N"),
        BIRTH_DELIVERY("BIRTH DELIVERY", "Birth Delivery", "BDL"),
        RESIDENCE_AT_BIRTH("RESIDENCE AT BIRTH", "Residence at Birth", "BR"),
        ORIGIN("ORIGIN", "Origin", "F"),
        LEGAL("LEGAL", "Legal", "L"),
        REGISTRY_HOME("REGISTRY HOME", "Registry Home", "RH"),
        BAD_ADDRESS("BAD ADDRESS", "Bad Address", "BA");

        private String value = null;
        private String printValue = null;
        private String hl7Value = null;

        AddressType(String value, String printValue, String hl7Value) {
            this.value = value;
            this.printValue = printValue;
            this.hl7Value = hl7Value;
        }

        public String getValue() {
            return this.value;
        }

        public String getPrintValue() {
            return this.printValue;
        }

        public String getHL7Value() {
            return this.hl7Value;
        }

        public static AddressType mapValueOf(String value) {
            if (null == value) {
                return UNKNOWN;
            } else {
                value = value.toUpperCase();
                AddressType[] types = values();
                AddressType[] arr$ = types;
                int len$ = types.length;

                for (int i$ = 0; i$ < len$; ++i$) {
                    AddressType type = arr$[i$];
                    if (type.getValue().equals(value) || type.getValue().startsWith(value)) {
                        return type;
                    }
                }

                if (value.equals("OFFICE")) {
                    return WORK;
                } else if (value.equals("RESIDENCE")) {
                    return HOME;
                } else {
                    return OTHER;
                }
            }
        }

        public static AddressType hl7ValueOf(String hl7Value) {
            if (hl7Value == null) {
                return UNKNOWN;
            } else {
                AddressType[] types = values();
                AddressType[] arr$ = types;
                int len$ = types.length;

                for (int i$ = 0; i$ < len$; ++i$) {
                    AddressType type = arr$[i$];
                    String value = type.getHL7Value();
                    if (value != null && type.getHL7Value().equalsIgnoreCase(hl7Value)) {
                        return type;
                    }
                }

                return UNKNOWN;
            }
        }

        public static AddressType mapValueOfB(String value) {
            if (null == value) {
                return UNKNOWN;
            } else {
                value = value.toUpperCase();
                AddressType[] types = values();
                AddressType[] arr$ = types;
                int len$ = types.length;

                for (int i$ = 0; i$ < len$; ++i$) {
                    AddressType type = arr$[i$];
                    if (type.getValue().equals(value) || type.getValue().startsWith(value)) {
                        return type;
                    }
                }

                if (value.equals("OFFICE")) {
                    return WORK;
                } else if (value.equals("RESIDENCE")) {
                    return HOME;
                } else {
                    return OTHER;
                }
            }
        }
    }
}
