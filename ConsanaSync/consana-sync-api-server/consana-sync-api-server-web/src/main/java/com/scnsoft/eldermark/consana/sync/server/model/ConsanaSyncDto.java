package com.scnsoft.eldermark.consana.sync.server.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import static java.util.Optional.ofNullable;

/**
 * This dto is is intended to represent sync api params
 */
@ApiModel(description = "This dto is is intended to represent a sync adsf")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2018-11-15T21:25:30.352+03:00[Europe/Minsk]")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
@EqualsAndHashCode
public class ConsanaSyncDto {
    @JsonProperty("identifier")
    private String identifier;

    //  @JsonProperty("organizationId")
    @JsonAlias({"organizationOID", "organizationId"})
    private String organizationId;

    //  @JsonProperty("communityId")
    @JsonAlias({"communityOID", "communityId"})
    private String communityId;

    @JsonProperty("updateType")
    private UpdateTypeEnum updateType;


    /**
     * Update type
     */
    public enum UpdateTypeEnum {
        PATIENT_UPDATE("PatientUpdate"),

        MAP_CLOSED("MAPClosed");

        private String value;

        UpdateTypeEnum(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static UpdateTypeEnum fromValue(String text) {
            for (UpdateTypeEnum b : UpdateTypeEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    /**
     * Patient cross refference identifier
     *
     * @return identifier
     **/
    @ApiModelProperty(example = "12345", value = "Xref patient identifier")


    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public ConsanaSyncDto organizationOID(String organizationOID) {
        this.organizationId = organizationOID;
        return this;
    }


    /**
     * Organization's consana_xowning_id
     *
     * @return organizationId
     **/
    @ApiModelProperty(example = "1.2.345677888", value = "Organization cross refference id specified in SC")


    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public ConsanaSyncDto communityOID(String communityOID) {
        this.communityId = communityOID;
        return this;
    }

    /**
     * Comunity's consana_org_id
     *
     * @return communityId
     **/
    @ApiModelProperty(example = "1.2.34567781234.6.7.8.9", value = "Community cross refference id specified in SC")


    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public ConsanaSyncDto updateType(UpdateTypeEnum updateType) {
        this.updateType = updateType;
        return this;
    }

    /**
     * Update type
     *
     * @return updateType
     **/
    @ApiModelProperty(example = "PatientUpdate", value = "Update type")


    public UpdateTypeEnum getUpdateType() {
        return updateType;
    }

    public void setUpdateType(UpdateTypeEnum updateType) {
        this.updateType = updateType;
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        return ofNullable(o)
                .map(obj -> obj.toString().replace("\n", "\n    "))
                .orElse("null");
    }
}

