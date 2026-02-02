package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.CcdCode;
import com.scnsoft.eldermark.shared.Gender;

import java.util.Collection;
import java.util.List;

/**
 * Created by pzhurba on 28-Sep-15.
 */
public interface CcdCodeDao {

    CcdCode getGenderCcdCode(Gender gender);

    CcdCode getMaritalStatus(String maritalStatus);

    List<CcdCode> getVitalSignTypes();

    CcdCode getCcdCode(String code, String codeSystem);

    CcdCode getCcdCode(String code, String codeSystem, String valueSet);

    CcdCode find(Long id);

    List<CcdCode> listCcdCodesByValueSetAndCodeSystem(String valueSetCode, String codeSystem);

    List<CcdCode> listWithSameDisplayName(Long codeId, Collection<String> codeSystems);

    List<CcdCode> listByCodeOrDisplayName(String searchString, Collection<String> codeSystems, int offset, int limit);

    Long countByCodeOrDisplayName(String searchString, Collection<String> codeSystems);

    List<CcdCode> getGenders();

    CcdCode getReference(Long id);

    CcdCode getRaceCcdCode(String raceString);

    CcdCode getReligionCcdCode(String religionString);

    CcdCode getEthnicGroup(String ethnicity);
}
