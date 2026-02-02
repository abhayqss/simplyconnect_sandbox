package com.scnsoft.eldermark.services.cda;

import com.scnsoft.eldermark.entity.CcdCode;
import org.eclipse.mdht.uml.cda.Observation;
import org.eclipse.mdht.uml.hl7.datatypes.CD;

import java.util.Collection;
import java.util.List;

/**
 * @author phomal
 * Created on 4/13/2018.
 */
public interface CcdCodeFactory {
    <T extends CD> CcdCode convert(T src);
    <T extends CD> CcdCode convert(T src, String hintValueSetOid);
    <T extends CD> List<CcdCode> convert(Collection<T> codes);
    List<CcdCode> convertInterpretationCodes(Observation ccdObservation);
}
