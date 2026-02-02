package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;

/**
 * This DTO is intended to represent a Care Team Member activity. (Note that Secure Messages are not tracked!)
 */
@ApiModel(description = "This DTO is intended to represent a Care Team Member activity. (Note that Secure Messages are not tracked!)")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-05-02T16:19:34.209+03:00")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true )
@JsonSubTypes({
  @JsonSubTypes.Type(value = EventActivityDto.class, name = "EVENT"),
  @JsonSubTypes.Type(value = CallActivityDto.class, name = "CALL"),
  @JsonSubTypes.Type(value = InvitationActivityDto.class, name = "INVITATION"),
  @JsonSubTypes.Type(value = VideoActivityDto.class, name = "VIDEO")
})
public class ActivityDto {

  /**
   * Activity type
   */
  public enum Type {
    CALL("CALL"),
    VIDEO("VIDEO"),
    EVENT("EVENT"),
    INVITATION("INVITATION");

    private final String value;

    Type(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static Type fromValue(String text) {
      for (Type b : Type.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  /**
   * Call type
   */
  public enum CallType {
    INCOMING("Incoming"),
    OUTGOING("Outgoing");

    private final String value;

    CallType(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static CallType fromValue(String text) {
      for (CallType b : CallType.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("type")
  private Type type = null;

  @JsonProperty("date")
  private Long date = null;


  @ApiModelProperty(required = true)
  @NotNull
  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  @ApiModelProperty
  public Long getDate() {
    return date;
  }

  public void setDate(Long date) {
    this.date = date;
  }

}

