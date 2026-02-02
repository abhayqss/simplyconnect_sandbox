package org.openhealthtools.openxds.dao;

import org.openhealthtools.openxds.entity.CcdCode;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;


public class CcdCodeDaoImpl extends HibernateDaoSupport implements CcdCodeDao {

    @Override
    public CcdCode findGenderByCode(String code) {
        List result = this.getHibernateTemplate().find(
                        "from CcdCode c where c.codeSystem='2.16.840.1.113883.5.1' and c.code = ?", code);

        return (result != null && !result.isEmpty()) ? (CcdCode) result.get(0) : null;
    }

    @Override
    public CcdCode findMaritalStatusByCode(String code) {
        List result = this.getHibernateTemplate().find(
                "from CcdCode c where c.codeSystem='2.16.840.1.113993.5.2' and c.code = ?", code);

        return (result != null && !result.isEmpty()) ? (CcdCode) result.get(0) : null;
    }

    @Override
    public CcdCode findRaceAndEthnicityByCode(String code) {
        List result = this.getHibernateTemplate().find(
                        "from CcdCode c where c.codeSystem='2.16.840.1.113883.6.238' and c.code = ?", code);

        return (result != null && !result.isEmpty()) ? (CcdCode) result.get(0) : null;
    }

    @Override
    public CcdCode findReligionByCode(String code) {
        List result = this.getHibernateTemplate().find(
                        "from CcdCode c where c.codeSystem='2.16.840.1.113883.5.1076' and c.code = ?", code);

        return (result != null && !result.isEmpty()) ? (CcdCode) result.get(0) : null;
    }

    @Override
    public CcdCode findLanguageByCode(String code) {
        List result = this.getHibernateTemplate().find(
                        "from CcdCode c where c.codeSystem='2.16.840.1.113883.6.121' and lower(c.code) = lower(?)", code);

        return (result != null && !result.isEmpty()) ? (CcdCode) result.get(0) : null;
    }

    @Override
    public CcdCode findMaritalStatusByName(String name) {
        List result = this.getHibernateTemplate().find(
                "from CcdCode c where c.codeSystem='2.16.840.1.113993.5.2' and  lower(c.displayName) = lower(?)", name);

        return (result != null && !result.isEmpty()) ? (CcdCode) result.get(0) : null;
    }

    @Override
    public CcdCode findRaceAndEthnicityByName(String name) {
        List result = this.getHibernateTemplate().find(
                "from CcdCode c where c.codeSystem='2.16.840.1.113883.6.238' and lower(c.displayName) = lower(?)", name);

        return (result != null && !result.isEmpty()) ? (CcdCode) result.get(0) : null;
    }

    @Override
    public CcdCode findReligionByName(String name) {
        List result = this.getHibernateTemplate().find(
                "from CcdCode c where c.codeSystem='2.16.840.1.113883.5.1076' and lower(c.displayName) = lower(?)", name);

        return (result != null && !result.isEmpty()) ? (CcdCode) result.get(0) : null;
    }

    @Override
    public CcdCode findLanguageByName(String name) {
        List result = this.getHibernateTemplate().find(
                "from CcdCode c where c.codeSystem='2.16.840.1.113883.6.121' and lower(c.displayName) = lower(?)", name);

        return (result != null && !result.isEmpty()) ? (CcdCode) result.get(0) : null;
    }

}
