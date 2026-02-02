package org.openhealthtools.openxds.registry.patient.parser.datatype;

import ca.uhn.hl7v2.model.AbstractPrimitive;
import ca.uhn.hl7v2.model.AbstractSegment;
import ca.uhn.hl7v2.model.v231.datatype.*;

/**
 * the service is used to find out whether the segment field is empty
 * @see org.openhealthtools.openxds.registry.patient.parser.segment.AdtSegmentParser#isHl7SegmentEmpty(AbstractSegment)
 */
public interface EmptyHL7Field231Service {

    boolean isTSEmpty(TS tsDate);

    boolean isCEEmpty(CE ce);

    boolean isCXEmpty(CX cx);

    boolean isCXArrayEmpty(CX[] cxArray);

    boolean isXPNEmpty(XPN xpn);

    boolean isXPNArrayEmpty(XPN[] xpnArray);

    boolean isHDEmpty(HD hd);

    boolean isFNEmpty(FN fn);

    boolean isAbstractPrimitiveEmpty(AbstractPrimitive abstractPrimitive);
}
