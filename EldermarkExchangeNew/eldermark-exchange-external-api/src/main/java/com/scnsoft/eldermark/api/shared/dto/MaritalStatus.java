package com.scnsoft.eldermark.api.shared.dto;

/**
 * Marital status display name
 */
public enum MaritalStatus {
  
  ANNULLED("Annulled", "A"),
  
  DIVORCED("Divorced", "D"),
  
  DOMESTIC_PARTNER("Domestic Partner", "T"),
  
  INTERLOCUTORY("Interlocutory", "I"),
  
  LEGALLY_SEPARATED("Legally Separated", "L"),
  
  MARRIED("Married", "M"),
  
  NEVER_MARRIED("Never Married", "S"),
  
  POLYGAMOUS("Polygamous", "P"),
  
  WIDOWED("Widowed", "W");

  private String value;
  private String ccdCode;

  MaritalStatus(String value, String ccdCode) {
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

