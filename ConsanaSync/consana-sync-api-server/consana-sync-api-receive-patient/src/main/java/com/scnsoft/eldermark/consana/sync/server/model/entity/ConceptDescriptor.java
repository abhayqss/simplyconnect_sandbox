package com.scnsoft.eldermark.consana.sync.server.model.entity;

/**
 * CONCEPT DESCRIPTOR (CD) is a coded entry data type, which supports the post-coordination of codes
 * (or, stated in another way, the combining of codes from a terminology to create a new concept).
 *
 * The use of post-coordination raises some interesting challenges in data extraction,
 * since many concepts that exist in the underlying terminology (such as “nodule-of-skin”) can also be constructed via
 * post-coordination (“lesion-of-skin :: Has-morphology :: nodule”).
 *
 * @author phomal
 * Created on 3/22/2017.
 */
public interface ConceptDescriptor {
    String getCode();
    String getDisplayName();
    String getCodeSystem();
    String getCodeSystemName();
    // String getCodeSystemVersion();
}
