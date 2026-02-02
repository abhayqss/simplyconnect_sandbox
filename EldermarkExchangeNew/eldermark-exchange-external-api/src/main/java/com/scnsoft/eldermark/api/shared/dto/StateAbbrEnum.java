package com.scnsoft.eldermark.api.shared.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * US state (abbreviation)
 */
public enum StateAbbrEnum {
  
  AL("AL"),
  
  AZ("AZ"),
  
  CA("CA"),
  
  CT("CT"),
  
  FL("FL"),
  
  GA("GA"),
  
  ID("ID"),
  
  IN("IN"),
  
  KS("KS"),
  
  NH("NH"),
  
  NM("NM"),
  
  ND("ND"),
  
  OK("OK"),
  
  OR("OR"),
  
  RI("RI"),
  
  SD("SD"),
  
  TN("TN"),
  
  UT("UT"),
  
  VA("VA"),
  
  WV("WV"),
  
  WY("WY"),
  
  ME("ME"),
  
  MA("MA"),
  
  MN("MN"),
  
  MO("MO"),
  
  NE("NE"),
  
  NV("NV"),
  
  NJ("NJ"),
  
  NY("NY"),
  
  NC("NC"),
  
  OH("OH"),
  
  PA("PA"),
  
  SC("SC"),
  
  VT("VT"),
  
  WA("WA"),
  
  WI("WI"),
  
  AK("AK"),
  
  AR("AR"),
  
  CO("CO"),
  
  DE("DE"),
  
  HI("HI"),
  
  IL("IL"),
  
  IA("IA"),
  
  KY("KY"),
  
  LA("LA"),
  
  MD("MD"),
  
  MI("MI"),
  
  MS("MS"),
  
  MT("MT"),
  
  TX("TX");

  private String value;

  StateAbbrEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static StateAbbrEnum fromValue(String text) {
    for (StateAbbrEnum b : StateAbbrEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}

