package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;
import javax.annotation.Generated;

/**
 * This DTO is intended to represent Advance Directive DETAILS
 */
@ApiModel(description = "This DTO is intended to represent Advance Directive DETAILS")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-06T15:56:03.515-03:00")
public class AdvanceDirectiveDto extends AdvanceDirectiveInfoDto {

    @JsonProperty("dataSource")
    private DataSourceDto dataSource = null;

    @JsonProperty("participants")
    private List<ParticipantListItemDto> participants = new ArrayList<ParticipantListItemDto>();

    @JsonProperty("externalDocuments")
    private List<ExternalDocumentDto> externalDocuments = new ArrayList<ExternalDocumentDto>();


    
   
    @ApiModelProperty(value = "")
    public DataSourceDto getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSourceDto dataSource) {
        this.dataSource = dataSource;
    }
    public AdvanceDirectiveDto addParticipantsItem(ParticipantListItemDto participantsItem) {
        this.participants.add(participantsItem);
        return this;
    }

    /**
    * Participants section represents a list of the participants.
    *
    * @return participants
    */
   
    @ApiModelProperty(value = "Participants section represents a list of the participants.")
    public List<ParticipantListItemDto> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ParticipantListItemDto> participants) {
        this.participants = participants;
    }
    public AdvanceDirectiveDto addExternalDocumentsItem(ExternalDocumentDto externalDocumentsItem) {
        this.externalDocuments.add(externalDocumentsItem);
        return this;
    }

    /**
    * represents a list of External Documents.
    *
    * @return externalDocuments
    */
   
    @ApiModelProperty(value = "represents a list of External Documents.")
    public List<ExternalDocumentDto> getExternalDocuments() {
        return externalDocuments;
    }

    public void setExternalDocuments(List<ExternalDocumentDto> externalDocuments) {
        this.externalDocuments = externalDocuments;
    }

}
