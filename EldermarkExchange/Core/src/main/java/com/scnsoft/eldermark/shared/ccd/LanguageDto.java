package com.scnsoft.eldermark.shared.ccd;

public class LanguageDto {
    private String code;

    private String abilityMode;

    private String abilityProficiency;

    private Boolean preferenceInd;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAbilityMode() {
        return abilityMode;
    }

    public void setAbilityMode(String abilityMode) {
        this.abilityMode = abilityMode;
    }

    public String getAbilityProficiency() {
        return abilityProficiency;
    }

    public void setAbilityProficiency(String abilityProficiency) {
        this.abilityProficiency = abilityProficiency;
    }

    public Boolean getPreferenceInd() {
        return preferenceInd;
    }

    public void setPreferenceInd(Boolean preferenceInd) {
        this.preferenceInd = preferenceInd;
    }
}
