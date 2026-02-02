package com.scnsoft.eldermark.h2.cda.util;

import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import org.eclipse.mdht.uml.cda.ClinicalDocument;
import org.eclipse.mdht.uml.cda.util.CDAUtil;
import org.eclipse.mdht.uml.hl7.datatypes.CD;
import org.eclipse.mdht.uml.hl7.datatypes.CE;
import org.eclipse.mdht.uml.hl7.rim.InfrastructureRoot;
import org.eclipse.mdht.uml.hl7.vocab.NullFlavor;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static junit.framework.TestCase.assertNull;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * @author phomal
 * Created on 5/15/2018.
 */
public class CdaTestUtils {

    private static final Logger logger = LoggerFactory.getLogger(CdaTestUtils.class);

    public static void assertCEMatches(String sectionName, CE ceExpected, CcdCode actual) {
        if (CcdParseUtils.hasContent(ceExpected)) {
            assertNotNull(sectionName, actual);
            assertEquals(sectionName + " code", ceExpected.getCode(), actual.getCode());
        } else {
            assertNull(sectionName, actual);
        }
    }

    public static void assertCollectionSizeMatches(String sectionName, List<? extends BasicEntity> actual, List<? extends InfrastructureRoot> expected) {
        if (CollectionUtils.isEmpty(expected)) {
            //noinspection unchecked
            assertThat(sectionName, actual, anyOf(nullValue(), empty()));
        } else {
            assertThat(sectionName, actual, hasSize(expected.size()));
        }
    }

    public static ClinicalDocument loadClinicalDocument(String... paths) throws Exception {
        final Path path = Paths.get("src/test/resources", paths);
        logger.info("Loading " + path.toString());

        final ClinicalDocument clinicalDocument = CDAUtil.load(new FileInputStream(path.toFile()));
        if (clinicalDocument != null && clinicalDocument.getVersionNumber() != null) {
            logger.info("***** Document version: " + clinicalDocument.getVersionNumber().getValue() + " *****");
        }

        return clinicalDocument;
    }

    public static void saveCcdDocument(ClinicalDocument ccd, String path) {
        try {
            CDAUtil.save(ccd, new FileOutputStream(path));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    public static void assertCodeTranslation(CcdCode entityCode, CD cdCode, boolean ceFilled) {
        if (ceFilled) {
            assertEquals(entityCode.getCode(), cdCode.getCode());
            assertEquals(entityCode.getCodeSystem(), cdCode.getCodeSystem());
        } else {
            Assert.assertNull(cdCode.getCode());
            Assert.assertNull(cdCode.getCodeSystem());
        }

        assertEquals(NullFlavor.OTH, cdCode.getNullFlavor());

        assertEquals(1, cdCode.getTranslations().size());

        var translation = cdCode.getTranslations().get(0);
        assertEquals(entityCode.getCode(), translation.getCode());
        assertEquals(entityCode.getCodeSystem(), translation.getCodeSystem());
    }
}
