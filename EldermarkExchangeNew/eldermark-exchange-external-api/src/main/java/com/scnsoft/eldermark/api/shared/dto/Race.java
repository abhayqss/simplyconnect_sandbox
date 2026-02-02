package com.scnsoft.eldermark.api.shared.dto;

public enum Race {
  
  AMERICAN_INDIAN_OR_ALASKA_NATIVE("American Indian or Alaska Native", "1002-5"),
  
  ASIAN("Asian", "2028-9"),
  
  BLACK_OR_AFRICAN_AMERICAN("Black or African-American", "2054-5"),
  
  NATIVE_HAWAIIAN_OR_OTHER_PACIFIC_ISLANDER("Native Hawaiian or Other Pacific Islander", "2076-8"),
  
  WHITE("White", "2106-3"),
  
  OTHER_RACE("Other Race", "2131-1");

  private String value;
  private String ccdCode;

  Race(String value, String ccdCode) {
    this.value = value;
    this.ccdCode = ccdCode;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  public String getCcdCode() {
    return ccdCode;
  }
}

