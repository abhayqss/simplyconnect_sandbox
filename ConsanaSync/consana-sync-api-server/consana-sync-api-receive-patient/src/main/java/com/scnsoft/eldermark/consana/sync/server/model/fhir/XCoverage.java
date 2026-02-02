package com.scnsoft.eldermark.consana.sync.server.model.fhir;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.api.annotation.*;
import ca.uhn.fhir.rest.gclient.ReferenceClientParam;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hl7.fhir.instance.model.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@ResourceDef(name = "XCoverage", profile = "http://hl7.org/fhir/profiles/consana/XCoverage")
public class XCoverage extends DomainResource {

    @Child(name = "class", type = {}, min = 0, max = 1, modifier = false, summary = false)
    @Description(shortDefinition = "Additional coverage classifications", formalDefinition = "A suite of underwrite specific classifiers, for example may be used to identify a class of coverage or employer group, Policy, Plan.")
    protected XCoverageClass class_;

    @Child(name = "beneficiary", type = { Patient.class }, min = 0, max = 1, modifier = false, summary = true)
    @Description(shortDefinition = "Plan Beneficiary", formalDefinition = "The party who benefits from the insurance coverage., the patient when services are provided.")
    protected Reference beneficiary;
    protected Patient beneficiaryTarget;

    @SearchParamDefinition(name = "beneficiary", path = "XCoverage.beneficiary", description = "Covered party", type = "reference", providesMembershipIn = { @Compartment(name = "Patient") }, target = { Patient.class })
    public static final String SP_BENEFICIARY = "beneficiary";
    public static final ReferenceClientParam BENEFICIARY = new ReferenceClientParam("beneficiary");
    public static final Include INCLUDE_BENEFICIARY = new Include("Coverage:beneficiary").toLocked();

    @SearchParamDefinition(name = "class", path = "XCoverage.grouping.class", description = "Class identifier", type = "string")
    public static final String SP_CLASS = "class";
    public static final StringClientParam CLASS = new StringClientParam("class");

    @Override
    public DomainResource copy() {
        var result = new XCoverage(class_, beneficiary, beneficiaryTarget);
        super.copyValues(result);
        return result;
    }

    @Override
    public ResourceType getResourceType() {
        return null;
    }
}
