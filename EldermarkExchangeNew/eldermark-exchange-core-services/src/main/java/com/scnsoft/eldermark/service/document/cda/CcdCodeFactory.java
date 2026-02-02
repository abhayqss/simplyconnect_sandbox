package com.scnsoft.eldermark.service.document.cda;

import java.util.Collection;
import java.util.List;

import org.eclipse.mdht.uml.cda.Observation;
import org.eclipse.mdht.uml.hl7.datatypes.CD;

import com.scnsoft.eldermark.entity.document.CcdCode;

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
