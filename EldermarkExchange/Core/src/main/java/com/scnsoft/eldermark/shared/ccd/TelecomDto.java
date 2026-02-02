package com.scnsoft.eldermark.shared.ccd;

public class TelecomDto {
    private String useCode;

    private String value;

    public String getUseCode() {
        return useCode;
    }

    public void setUseCode(String useCode) {
        this.useCode = useCode;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TelecomDto that = (TelecomDto) o;

        if (getUseCode() != null ? !getUseCode().equals(that.getUseCode()) : that.getUseCode() != null) {
            return false;
        }
        return getValue() != null ? getValue().equals(that.getValue()) : that.getValue() == null;
    }

    @Override
    public int hashCode() {
        int result = getUseCode() != null ? getUseCode().hashCode() : 0;
        result = 31 * result + (getValue() != null ? getValue().hashCode() : 0);
        return result;
    }
}
