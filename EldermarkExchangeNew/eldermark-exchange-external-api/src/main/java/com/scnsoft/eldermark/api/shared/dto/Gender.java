package com.scnsoft.eldermark.api.shared.dto;

/**
 * Gender display name
 */
public enum Gender {
  
  MALE("Male", "F"),
  
  FEMALE("Female", "M"),
  
  UNDIFFERENTIATED("Undifferentiated", "UN");

  private String value;
  private String ccdCode;

  Gender(String value, String ccdCode) {
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

