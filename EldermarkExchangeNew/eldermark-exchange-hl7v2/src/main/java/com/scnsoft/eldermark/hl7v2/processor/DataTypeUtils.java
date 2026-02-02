package com.scnsoft.eldermark.hl7v2.processor;

import com.scnsoft.eldermark.entity.xds.datatype.CECodedElement;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataTypeUtils {
    private static final Logger logger = LoggerFactory.getLogger(DataTypeUtils.class);

    public static boolean isEmpty(CECodedElement ce) {
        if (ce == null) {
            logger.info("CE is null");
            return true;
        }

        var hasFirstCode = !StringUtils.isAllEmpty(
                ce.getIdentifier(),
                ce.getText(),
                ce.getNameOfCodingSystem()
        );
        if (hasFirstCode) {
            return false;
        }

        logger.info("First CE code is empty");

        var hasSecondCode = !StringUtils.isAllEmpty(
                ce.getAlternateIdentifier(),
                ce.getAlternateText(),
                ce.getNameOfAlternateCodingSystem()
        );
        if (hasSecondCode) {
            return false;
        }
        logger.info("Second CE code is empty");

        return true;
    }
}
