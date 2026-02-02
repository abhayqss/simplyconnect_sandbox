package com.scnsoft.eldermark.service.inbound.healthpartners;

import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.dao.CommunityDao;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.Map;

@Service
@ConditionalOnProperty(
        value = "healthPartners.integration.enabled",
        havingValue = "true"
)
public class HpFileTargetResolverImpl implements HpFileTargetResolver {

    private final Map<HpFileSource, Pair<String, String>> fileSourceTargetCache;

    private final CommunityDao communityDao;

    @Autowired
    public HpFileTargetResolverImpl(
            @Value("${healthPartners.dest.org.alternativeId}") String hpOrgAlternativeId,
            @Value("${healthPartners.dest.community.oid}") String hpCommunityOid,
            @Value("${healthPartners.test.dest.org.alternativeId}") String hpTestOrgAlternativeId,
            @Value("${healthPartners.test.dest.community.oid}") String hpTestCommunityOid,
            CommunityDao communityDao) {
        fileSourceTargetCache = new EnumMap<>(HpFileSource.class);
        fileSourceTargetCache.put(HpFileSource.SFTP, new Pair<>(hpOrgAlternativeId, hpCommunityOid));
        fileSourceTargetCache.put(HpFileSource.TESTING, new Pair<>(hpTestOrgAlternativeId, hpTestCommunityOid));

        this.communityDao = communityDao;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Long resolveTargetCommunityId(HpFileSource fileSource) {
        var target = fileSourceTargetCache.getOrDefault(fileSource, null);
        if (target == null) {
            throw new HpFileProcessingException("Failed to resolve target organization alternative id and community oid");
        }

        return communityDao.findByOrganization_AlternativeIdAndOid(target.getFirst(), target.getSecond(), IdAware.class)
                .orElseThrow(() -> new HpFileProcessingException(
                        "Failed to load target commnity by " +
                                "orgAlternativeId=" + target.getFirst() +
                                " and oid=" + target.getSecond())
                ).getId();
    }
}
