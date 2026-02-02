package com.scnsoft.eldermark.consana.sync.server.model.fhir;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.ResourceDef;
import ca.uhn.fhir.model.api.annotation.SearchParamDefinition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hl7.fhir.instance.model.DomainResource;
import org.hl7.fhir.instance.model.ResourceType;
import org.hl7.fhir.instance.model.StringType;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@ResourceDef(name = "XOwningOrganization", profile = "http://hl7.org/fhir/profiles/consana/XOwningOrganization")
public class XOwningOrganization extends DomainResource {

    @Child(name = "externalId")
    private StringType externalId;

    @Child(name = "simplyConnectId")
    private StringType simplyConnectId;

    public XOwningOrganization(String externalId, String simplyConnectId) {
        this(new StringType(externalId), new StringType(simplyConnectId));
    }

    @SearchParamDefinition(
            name = "scid",
            path = "XOwningOrganization.simplyConnectId",
            description = "Simply connect oid"
    )
    public static final String SP_SIMPLYCONNECT_OID = "scid";

    @Override
    public XOwningOrganization copy() {
        var result = new XOwningOrganization(externalId, simplyConnectId);
        super.copyValues(result);
        return result;
    }

    @Override
    public ResourceType getResourceType() {
        return null;
    }

}
