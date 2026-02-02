package com.scnsoft.eldermark.service.rxnorm;

import com.scnsoft.eldermark.dao.NationalDrugCodeDao;
import com.scnsoft.eldermark.entity.NationalDrugCode;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;
import com.scnsoft.eldermark.service.document.cda.CcdCodeCustomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class NdcToRxnormResolverImpl implements NdcToRxnormResolver {
    private static final Logger logger = LoggerFactory.getLogger(NdcToRxnormResolverImpl.class);

    private static final List<String> resolvableNDCStatuses = List.of("ACTIVE", "OBSOLETE");

    @Autowired
    private RxNormApiGateway rxNormApiGateway;

    @Autowired
    private CcdCodeCustomService ccdCodeService;

    @Autowired
    private NationalDrugCodeDao nationalDrugCodeDao;

    @Autowired
    private CachingRxNormVersionResolver cachingRxNormVersionResolver;

    @Override
    @Transactional
    public Optional<CcdCode> resolve(String nationalDrugCode) {
        logger.info("Resolving NDC -> RxNorm {}", nationalDrugCode);

        var datasetVersion = cachingRxNormVersionResolver.getRxNormVersion();

        var ndcCached = nationalDrugCodeDao.findFirstByNationalDrugCode(nationalDrugCode);

        if (ndcCached.isPresent() && datasetVersion.equals(ndcCached.get().getDatasetVersion())) {
            logger.info("Resolved NDC from cache");
            return Optional.ofNullable(ndcCached.get().getRxNormCcdCode());
        }

        var ndcEntity = ndcCached.orElseGet(() -> {
                    var e = new NationalDrugCode();
                    e.setNationalDrugCode(nationalDrugCode);
                    return e;
                }
        );

        ndcEntity.setDatasetVersion(datasetVersion);
        logger.info("Fetching ndcStatus from API");
        var ndcStatus = rxNormApiGateway.getNDCStatus(nationalDrugCode);
        logger.info("NDC API done");

        ndcEntity.setStatus(ndcStatus.getStatus());

        if (resolvableNDCStatuses.contains(ndcStatus.getStatus())) {
            if (ndcEntity.getRxNormCcdCode() != null && Objects.equals(ndcStatus.getCode(), ndcEntity.getRxNormCcdCode().getCode())) {
                logger.info("Code update not needed because they match");
            } else {
                var code = ccdCodeService.findOrCreate(ndcStatus.getCode(), ndcStatus.getDisplayName(), CodeSystem.RX_NORM);
                logger.info("Code found or created");
                ndcEntity.setRxNormCcdCode(code.orElse(null));
            }

        } else {
            logger.info("NDC not resolved");
            ndcEntity.setRxNormCcdCode(null);
        }
        nationalDrugCodeDao.save(ndcEntity);

        return Optional.ofNullable(ndcEntity.getRxNormCcdCode());
    }
}
