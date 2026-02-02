package org.openhealthtools.openxds.dao;

import org.openhealthtools.openxds.entity.CcdCode;


public interface CcdCodeDao {
    CcdCode findGenderByCode(String code);

    CcdCode findMaritalStatusByCode(String code);

    CcdCode findRaceAndEthnicityByCode(String code);

    CcdCode findReligionByCode(String code);

    CcdCode findLanguageByCode(String code);

    CcdCode findMaritalStatusByName(String name);

    CcdCode findRaceAndEthnicityByName(String name);

    CcdCode findReligionByName(String name);

    CcdCode findLanguageByName(String name);


}
