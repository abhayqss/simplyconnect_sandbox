package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.ObjectUtils;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-05-04T15:41:27.414+03:00")
public class ProblemInfoDto implements Comparable<ProblemInfoDto>{

  @JsonProperty("id")
  private Long id = null;

  @JsonProperty("diagnosisCode")
  private String diagnosisCode = null;

  @JsonProperty("diagnosisCodeSet")
  private String diagnosisCodeSet = null;

  @JsonProperty("problemName")
  private String problemName = null;

  /**
   * Problem type. It contains a single value.
   */
  public enum ProblemType {
    CONDITION("Condition"),
    SYMPTOM("Symptom"),
    FINDING("Finding"),
    COMPLAINT("Complaint"),
    FUNCTIONAL_LIMITATION("Functional limitation"),
    PROBLEM("Problem"),
    DIAGNOSIS("Diagnosis");

    private final String value;

    ProblemType(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static ProblemType fromValue(String text) {
      for (ProblemType b : ProblemType.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("problemType")
  private String problemType = null;

  @JsonProperty("ageObservationUnit")
  private String ageObservationUnit = null;

  @JsonProperty("ageObservationValue")
  private Integer ageObservationValue = null;

  @JsonProperty("dataSource")
  private DataSourceDto dataSource = null;

  @JsonProperty("period")
  private PeriodDto period = null;

  /**
   * Health status. The status of the patient overall as a result of a particular problem. (It's not widely used)
   */
  public enum HealthStatusObservation {
    ALIVE_AND_WELL("Alive and well"),
    IN_REMISSION("In remission"),
    SYMPTOM_FREE("Symptom free"),
    CHRONICALLY_ILL("Chronically ill"),
    SEVERELY_ILL("Severely ill"),
    DISABLED("Disabled"),
    SEVERELY_DISABLED("Severely disabled"),
    DECEASED("Deceased");

    private final String value;

    HealthStatusObservation(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static HealthStatusObservation fromValue(String text) {
      for (HealthStatusObservation b : HealthStatusObservation.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("healthStatusObservation")
  private String healthStatusObservation = null;

  /**
   * Problem status
   */
  public enum Status {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    CHRONIC("Chronic"),
    INTERMITTENT("Intermittent"),
    RECURRENT("Recurrent"),
    RULE_OUT("Rule out"),
    RULED_OUT("Ruled out"),
    RESOLVED("Resolved");

    private final String value;

    Status(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static Status fromValue(String text) {
      for (Status b : Status.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("status")
  private String status = null;

  @JsonProperty("translations")
  private Map<String, String> translations = new HashMap<String, String>();


  @ApiModelProperty
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Diagnosis code. It contains a single value.
   * @return diagnosisCode
   */
  @ApiModelProperty(example = "A50.01", value = "Diagnosis code. It contains a single value.")
  public String getDiagnosisCode() {
    return diagnosisCode;
  }

  public void setDiagnosisCode(String diagnosisCode) {
    this.diagnosisCode = diagnosisCode;
  }

  /**
   * Diagnosis code set (Code system name). It contains a single value.
   * @return diagnosisCodeSet
   */
  @ApiModelProperty(example = "ICD 10 CM", value = "Diagnosis code set (Code system name). It contains a single value.")
  public String getDiagnosisCodeSet() {
    return diagnosisCodeSet;
  }

  public void setDiagnosisCodeSet(String diagnosisCodeSet) {
    this.diagnosisCodeSet = diagnosisCodeSet;
  }

  /**
   * Problem name. It contains a display name of the Diagnosis code.
   * @return problemName
   */
  @ApiModelProperty(example = "Early congenital syphilitic oculopathy", value = "Problem name. It contains a display name of the Diagnosis code.")
  public String getProblemName() {
    return problemName;
  }

  public void setProblemName(String problemName) {
    this.problemName = problemName;
  }

  /**
   * Problem type. It contains a single value.
   * @return problemType
   */
  @ApiModelProperty(value = "Problem type. It contains a single value.")
  public String getProblemType() {
    return problemType;
  }

  public void setProblemType(String problemType) {
    this.problemType = problemType;
  }

  /**
   * Age observation unit
   * @return ageObservationUnit
   */
  @ApiModelProperty(example = "a", value = "Age observation unit")
  public String getAgeObservationUnit() {
    return ageObservationUnit;
  }

  public void setAgeObservationUnit(String ageObservationUnit) {
    this.ageObservationUnit = ageObservationUnit;
  }

  /**
   * Age observation value
   * @return ageObservationValue
   */
  @ApiModelProperty(example = "75", value = "Age observation value")
  public Integer getAgeObservationValue() {
    return ageObservationValue;
  }

  public void setAgeObservationValue(Integer ageObservationValue) {
    this.ageObservationValue = ageObservationValue;
  }

  @ApiModelProperty
  public DataSourceDto getDataSource() {
    return dataSource;
  }

  public void setDataSource(DataSourceDto dataSource) {
    this.dataSource = dataSource;
  }

  @ApiModelProperty
  public PeriodDto getPeriod() {
    return period;
  }

  public void setPeriod(PeriodDto period) {
    this.period = period;
  }

  /**
   * Health status. The status of the patient overall as a result of a particular problem. (It's not widely used)
   * @return healthStatusObservation
   */
  @ApiModelProperty(example = "Alive and well", value = "Health status. The status of the patient overall as a result of a particular problem. (It's not widely used)")
  public String getHealthStatusObservation() {
    return healthStatusObservation;
  }

  public void setHealthStatusObservation(String healthStatusObservation) {
    this.healthStatusObservation = healthStatusObservation;
  }

  /**
   * Problem status
   * @return status
   */
  @ApiModelProperty(value = "Problem status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public ProblemInfoDto putTranslationsItem(String key, String translationsItem) {
    this.translations.put(key, translationsItem);
    return this;
  }

  /**
   * A collection of translations for the Diagnosis code. It's a map of Codes to Code system names.
   * @return translations
   */
  @ApiModelProperty(value = "A collection of translations for the Diagnosis code. It's a map of Codes to Code system names.")
  public Map<String, String> getTranslations() {
    return translations;
  }

  public void setTranslations(Map<String, String> translations) {
    this.translations = translations;
  }

  @Override
  public int compareTo(ProblemInfoDto o) {
    int result = ObjectUtils.compare(this.period.getStartDate(), o.period.getStartDate());
    if (result != 0) return result;
    return ObjectUtils.compare(this.problemName, o.problemName);
  }
}

