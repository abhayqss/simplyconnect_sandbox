package com.scnsoft.eldermark.consana.sync.server.model.fhir;

import ca.uhn.fhir.model.api.annotation.Block;
import ca.uhn.fhir.model.api.annotation.Child;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hl7.fhir.instance.model.BackboneElement;
import org.hl7.fhir.instance.model.StringType;
import org.hl7.fhir.instance.model.api.IBaseBackboneElement;

import static java.util.Optional.ofNullable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Block
public class XCoverageClass extends BackboneElement implements IBaseBackboneElement {

    @Child(name = "value", type = { StringType.class })
    private StringType value;

    @Child(name = "name", type = { StringType.class })
    private StringType name;

    public XCoverageClass copy() {
        final XCoverageClass dst = new XCoverageClass();
        this.copyValues(dst);
        dst.name = ofNullable(this.name).map(n -> n.copy()).orElse(null);
        dst.value = ofNullable(this.value).map(v -> v.copy()).orElse(null);
        return dst;
    }
}
