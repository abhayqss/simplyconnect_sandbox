package com.scnsoft.eldermark.hl7v2.processor;

import com.scnsoft.eldermark.dao.CcdCodeDao;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.xds.datatype.CECodedElement;
import com.scnsoft.eldermark.service.document.cda.CcdCodeCustomService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CcdCodeResolverServiceImpl implements CcdCodeResolverService {
    private static final Logger logger = LoggerFactory.getLogger(CcdCodeResolverServiceImpl.class);
    @Autowired
    private CcdCodeDao ccdCodeDao;

    @Autowired
    private CcdCodeCustomService ccdCodeCustomService;

    @Override
    public Optional<CcdCode> resolveCode(CECodedElement ce) {
        if (ce == null) {
            return Optional.empty();
        }
        return resolveCode(ce.getIdentifier(), ce.getText(), ce.getNameOfCodingSystem())
                .or(() -> resolveCode(ce.getAlternateIdentifier(), ce.getAlternateText(), ce.getNameOfAlternateCodingSystem()));
    }

    private Optional<CcdCode> resolveCode(String code, String name, String codeSystemName) {
        if (StringUtils.isAllEmpty(code, codeSystemName)) {
            logger.info("Code and code system for ccd code are empty");
            return Optional.empty();
        }

        var resolvedCodeSystem = CodeSystemMappingUtil.resolveCodeSystem(codeSystemName);
        if (resolvedCodeSystem.isEmpty()) {
            logger.info("Unknown code system {}", codeSystemName);
            return Optional.empty();
        }

        if (StringUtils.isEmpty(name)) {
            return resolvedCodeSystem.map(codeSystem -> ccdCodeDao.findFirstByCodeAndCodeSystem(code, codeSystem.getOid()));
        } else {
            return resolvedCodeSystem
                    .flatMap(codeSystem -> ccdCodeCustomService.findOrCreate(code, name, codeSystem));
        }
    }
}
