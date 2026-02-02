package com.scnsoft.eldermark.services.cda.util;

import com.scnsoft.eldermark.entity.CcdCode;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author averazub
 * @author phomal
 * Created on 1/23/2017.
 */
public class CcdTransform {
    private static final Logger logger = LoggerFactory.getLogger(CcdTransform.class);


    /**
     * Convert ED to String.
     * <br/>
     * Implementation details:
     * <ol>
     *      <li>ED > text is returned if present;</li>
     *      <li>if ED is null or ED > text is empty, then the first non-null defaultValue is returned.</li>
     * </ol>
     * @see ED
     */
    public static String EDtoString(ED src, String... defaultValues) {
        String text = CcdParseUtils.hasContent(src) ? src.getText() : null;
        return StringUtils.trim(StringUtils.defaultIfBlank(text, ObjectUtils.firstNonNull(defaultValues)));
    }

    /**
     * Convert ED to String.
     * <br/>
     * Implementation details:
     * <ol>
     *      <li>ED > text is returned if present;</li>
     *      <li>if ED is null or ED > text is empty, then code > displayName is returned;</li>
     *      <li>if code is null, then null is returned.</li>
     * </ol>
     * @see ED
     */
    public static String EDtoString(ED src, CcdCode code) {
        return EDtoString(src, code == null ? null : code.getDisplayName());
    }

    /**
     * Convert ED to String.
     * <br/>
     * Implementation details:
     * <ol>
     *      <li>ED > text is returned if present;</li>
     *      <li>if ED is null or ED > text is empty, then null is returned;</li>
     * </ol>
     * @see ED
     */
    public static String EDtoString(ED src) {
        return EDtoString(src, (String) null);
    }

    /**
     * Convert IVL_TS to a date pair.
     * <br/>
     * Example from CCD:
     * <pre>
     * {@code <effectiveTime>
     *     <low value="1998"/>
     *     <high value="2018"/>
     * </effectiveTime>}</pre>
     * <br/>
     * Implementation details:
     * <ol>
     *      <li>IVL_TS > high (if present) is parsed and returned as the first date;</li>
     *      <li>IVL_TS > low (if present) is parsed and returned as the second date;</li>
     *      <li>if IVL_TS is null, then null is returned.</li>
     * </ol>
     * @see IVL_TS
     */
    public static Pair<Date, Date> IVLTStoHighLowDate(IVL_TS src) {
        if (CcdParseUtils.hasContent(src)) {
            Date low = null;
            Date high = null;

            if (src.getHigh() != null) {
                final String effectiveTimeHigh = src.getHigh().getValue();
                high = CcdParseUtils.parseDate(effectiveTimeHigh);
            }

            if (src.getLow() != null) {
                final String effectiveTimeLow = src.getLow().getValue();
                low = CcdParseUtils.parseDate(effectiveTimeLow);
            }

            return new Pair<>(high, low);
        }
        return null;
    }

    /**
     * Fetch center date from IVL_TS.
     * <br/>
     * Example from CCD:
     * <pre>
     * {@code <effectiveTime>
     *     <center value="1998"/>
     * </effectiveTime>}</pre>
     * <br/>
     * @see IVL_TS
     */
    public static Date IVLTStoCenterDate(IVL_TS src) {
        if (CcdParseUtils.hasContent(src) && src.getCenter() != null) {
            return CcdParseUtils.parseDate(src.getCenter().getValue());
        }
        return null;
    }

    /**
     * Fetch center date or value from effectiveTimefrom IVL_TS
     * <br/>
     * Example from CCD:
     * <pre>
     * {@code <effectiveTime>
     *     <center value="1998"/>
     * </effectiveTime>}</pre>
     * <br/>
     * <pre>
     * {@code <effectiveTime value="2003"/>}</pre>
     * <br/>
     * @see IVL_TS
     */
    public static Date IVLTStoCenterDateOrTsToDate(IVL_TS src) {
        final Date center = IVLTStoCenterDate(src);
        if (center == null) {
            return CcdParseUtils.convertTsToDate(src);
        }
        return null;
    }

    /**
     * Convert IVL_TS to a date pair of (high.value, low.value) if these values are present, or to pair (null, center.value)
     * or to to pair (null, value).
     * Return value is never null.
     *
     * <br/>
     * Example from CCD:
     * <pre>
     * {@code <effectiveTime>
     *     <low value="1998"/>
     *     <high value="2018"/>
     * </effectiveTime>}</pre>
     * Result: (2018, 1998)
     * <br/>
     * <pre>
     * {@code <effectiveTime>
     *     <center value="2001"/>
     * </effectiveTime>}</pre>
     * Result: (null, 2001)
     * <br/>
     * <pre>
     * {@code <effectiveTime value="2013"/>}</pre>
     * <br/>
     * Result: (null, 2013)
     * @see IVL_TS
     */
    public static Pair<Date, Date> IVLTStoHighLowDateOrIVLTStoCenterDateOrTsToDate(IVL_TS src) {
        Pair<Date, Date> result = IVLTStoHighLowDate(src);
        if (result != null && (result.getFirst() != null || result.getSecond() != null)) {
            return result;
        }
        return new Pair<>(null, IVLTStoCenterDateOrTsToDate(src));
    }

    /**
     * Convert IVL_TS to a date pair of (high, low) if these values are present, of to pair (null, value) otherwise.
     * Return value is never null.
     *
     * <br/>
     * Example from CCD:
     * <pre>
     * {@code <effectiveTime>
     *     <low value="1998"/>
     *     <high value="2018"/>
     * </effectiveTime>}</pre>
     * Result: (2018, 1998)
     * <br/>
     * <pre>
     * {@code <effectiveTime value="2013"/>}</pre>
     * <br/>
     * Result: (null, 2013)
     * Implementation details:
     * <ol>
     *      <li>IVL_TS > high (if present) is parsed and returned as the first date;</li>
     *      <li>IVL_TS > low (if present) is parsed and returned as the second date;</li>
     *      <li>if IVL_TS is null, then null is returned.</li>
     * </ol>
     * @see IVL_TS
     */
    public static Pair<Date, Date> IVLTStoHighLowDateOrTsToDate(IVL_TS src) {
        Pair<Date, Date> result = IVLTStoHighLowDate(src);
        if (result != null && (result.getFirst() != null || result.getSecond() != null)) {
            return result;
        }
        return new Pair<>(null, CcdParseUtils.convertTsToDate(src));
    }

    /**
     * @deprecated Use IVLTStoHighLowDate() or write custom code for SXCM_TS parsing
     */
    public static Pair<Date, Date> SXCM_TStoHighLowDate(SXCM_TS src) {
        if (src instanceof IVL_TS) {
            return IVLTStoHighLowDate((IVL_TS) src);
        }
        if (CcdParseUtils.hasContent(src)) {
            String dateStrHigh = src.getValue();
            Date high = null;
            if (dateStrHigh != null)
                high = CcdParseUtils.parseDate(src.getValue());

            return new Pair<>(high, null);
        }
        return null;
    }

    public static Integer INTtoInteger(INT src) {
        if (CcdParseUtils.hasContent(src) && src.getValue() != null) {
            return src.getValue().intValue();
        }
        return null;
    }

    public static Integer PQtoInteger(PQ src) {
        if (CcdParseUtils.hasContent(src) && src.getValue() != null) {
            return src.getValue().intValue();
        }
        return null;
    }

    public static com.scnsoft.eldermark.entity.Organization toOrganization(
            org.eclipse.mdht.uml.cda.Organization organization, Database database, String legacyTable) {
        if (!CcdParseUtils.hasContent(organization)) {
            return null;
        }

        EList<ON> names = organization.getNames();
        EList<AD> addresses = organization.getAddrs();
        EList<TEL> telecoms = organization.getTelecoms();
        String legacyId = CcdParseUtils.getFirstIdExtensionStr(organization.getIds());

        return CcdParseUtils.createOrganization(names, addresses, telecoms, database, legacyTable, legacyId);
    }

    public static com.scnsoft.eldermark.entity.Organization toOrganization(
            org.eclipse.mdht.uml.cda.CustodianOrganization organization, Database database, String legacyTable) {
        if (!CcdParseUtils.hasContent(organization)) {
            return null;
        }

        String legacyId = CcdParseUtils.getFirstIdExtensionStr(organization.getIds());
        return CcdParseUtils.createOrganization(organization.getName(), organization.getAddrs(),
                organization.getTelecom(), database, legacyTable, legacyId);
    }

}
