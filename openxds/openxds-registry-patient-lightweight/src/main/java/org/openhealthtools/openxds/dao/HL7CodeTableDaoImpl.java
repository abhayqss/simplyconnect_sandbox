package org.openhealthtools.openxds.dao;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.Restrictions;
import org.openhealthtools.openxds.entity.hl7table.HL7CodeTable;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;
import java.util.logging.Logger;

public class HL7CodeTableDaoImpl extends HibernateDaoSupport implements HL7CodeTableDao {
    private static final Logger log = Logger.getLogger(HL7CodeTableDaoImpl.class.getName());

    @Override
    public <T extends HL7CodeTable> T findCode(String code, Class<T> tableClass) {
        if (code == null) {
            return null;
        }
        final List<HL7CodeTable> codes = getSession()
                .createCriteria(tableClass)
                .add(Restrictions.eq("code", code))
                .list();
        if (CollectionUtils.isEmpty(codes)) {
            return null;
        }
        if (codes.size() > 1) {
            log.warning(String.format("HL7 code %s is present multiple times for table %s", code, tableClass.getSimpleName()));
        }
        return (T) codes.get(0);
    }

    @Override
    public HL7CodeTable findCode(String code, List<Class<? extends HL7CodeTable>> tableClasses) {
        if (CollectionUtils.isEmpty(tableClasses)) {
            return null;
        }

        /*
            [update hint] As JPA 1.0 does not support 'TYPE()' function, we have to iterate all classes in code instead
            of allowing DB to the job. Please use 'TYPE()' after updating.
         */
        for (Class<? extends HL7CodeTable> tableClass : tableClasses) {
            final HL7CodeTable hl7Code = findCode(code, tableClass);
            if (hl7Code != null) {
                return hl7Code;
            }
        }
        return null;
    }
}
