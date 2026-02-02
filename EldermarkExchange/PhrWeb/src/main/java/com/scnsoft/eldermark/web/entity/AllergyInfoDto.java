package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.ObjectUtils;

import javax.annotation.Generated;


/**
 * This DTO is intended to represent allergies
 */
@ApiModel(description = "This DTO is intended to represent allergies")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-05-19T12:25:10.264+03:00")
public class AllergyInfoDto implements Comparable<AllergyInfoDto> {

    @JsonProperty("productText")
    private String productText = null;

    @JsonProperty("allergyType")
    private String allergyType = null;

    /**
     * Allergen type
     */
    public enum AllergenType {
        DRUG("DRUG"),
        FOOD("FOOD"),
        ENVIRONMENT("ENVIRONMENT");

        private final String value;

        AllergenType(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static AllergenType fromValue(String text) {
            for (AllergenType b : AllergenType.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    @JsonProperty("allergenType")
    private AllergenType allergenType = null;

    @JsonProperty("reaction")
    private String reaction = null;

    @JsonProperty("endDate")
    private Long endDate = null;

    @JsonProperty("dataSource")
    private DataSourceDto dataSource = null;


    /**
     * Allergen type
     * @return allergenType
     */
    @ApiModelProperty(value = "Allergen type")
    public AllergenType getAllergenType() {
        return allergenType;
    }

    public void setAllergenType(AllergenType allergenType) {
        this.allergenType = allergenType;
    }

    @ApiModelProperty(example = "Amoxicillin")
    public String getProductText() {
        return productText;
    }

    public void setProductText(String productText) {
        this.productText = productText;
    }

    @ApiModelProperty(example = "Drug allergy")
    public String getAllergyType() {
        return allergyType;
    }

    public void setAllergyType(String allergyType) {
        this.allergyType = allergyType;
    }

    @ApiModelProperty(example = "Rash")
    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public DataSourceDto getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSourceDto dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public int compareTo(AllergyInfoDto o) {
        return ObjectUtils.compare(this.productText, o.productText);
    }
}

