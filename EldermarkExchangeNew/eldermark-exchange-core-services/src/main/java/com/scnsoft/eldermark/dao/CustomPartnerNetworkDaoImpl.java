package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.beans.PartnerNetworkOrganization;
import com.scnsoft.eldermark.entity.Organization_;
import com.scnsoft.eldermark.entity.PartnerNetworkCommunity;
import com.scnsoft.eldermark.entity.PartnerNetworkCommunity_;
import com.scnsoft.eldermark.entity.basic.DisplayableNamedEntity;
import com.scnsoft.eldermark.entity.basic.DisplayableNamedEntity_;
import com.scnsoft.eldermark.entity.community.Community_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Selection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CustomPartnerNetworkDaoImpl implements CustomPartnerNetworkDao {

    @Autowired
    private EntityManager entityManager;

    @Override
    public Page<PartnerNetworkOrganization> findGroupedByOrganization(Specification<PartnerNetworkCommunity> specification,
                                                                      Pageable pageable) {
        var cb = entityManager.getCriteriaBuilder();

        var organizationQuery = createOrganizationsQuery(specification, pageable, cb);
        var countQuery = createOrganizationsCountQuery(specification, cb);

        var organizationsPage = PageableExecutionUtils.getPage(organizationQuery.getResultList(), pageable, countQuery::getSingleResult)
                .map(PartnerNetworkOrganization::new);

        fillCommunities(organizationsPage.getContent(), specification, cb);

        return organizationsPage;
    }

    private TypedQuery<Tuple> createOrganizationsQuery(Specification<PartnerNetworkCommunity> specification,
                                                                            Pageable pageable,
                                                                            CriteriaBuilder cb) {
        var organizationQuery = cb.createQuery(Tuple.class);
        var root = organizationQuery.from(PartnerNetworkCommunity.class);
        var organizationJoin = root.join(PartnerNetworkCommunity_.community).join(Community_.organization);

        var sort = QueryUtils.toOrders(pageable.getSort(), organizationJoin, cb);

        var selections = new ArrayList<Selection<?>>();
        selections.add(organizationJoin.get(Organization_.id).alias(DisplayableNamedEntity_.ID));
        selections.add(organizationJoin.get(Organization_.name).alias(DisplayableNamedEntity_.DISPLAY_NAME));
        sort.stream()
                .filter(o -> !Arrays.asList(DisplayableNamedEntity_.ID, DisplayableNamedEntity_.DISPLAY_NAME)
                        .contains(o.getExpression().getAlias()))
                .forEach(o -> selections.add(o.getExpression()));

        organizationQuery.multiselect(selections);
        organizationQuery.where(specification.toPredicate(root, organizationQuery, cb));
        organizationQuery.distinct(true);
        organizationQuery.orderBy(sort);

        var query = entityManager.createQuery(organizationQuery);
        if (pageable.isPaged()) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }
        return query;
    }

    private TypedQuery<Long> createOrganizationsCountQuery(Specification<PartnerNetworkCommunity> specification,
                                                           CriteriaBuilder cb) {
        var organizationQuery = cb.createQuery(Long.class);
        var root = organizationQuery.from(PartnerNetworkCommunity.class);
        var organizationJoin = root.get(PartnerNetworkCommunity_.community).get(Community_.organization);

        organizationQuery.select(cb.countDistinct(organizationJoin.get(Organization_.id)));
        organizationQuery.where(specification.toPredicate(root, organizationQuery, cb));

        return entityManager.createQuery(organizationQuery);
    }


    private void fillCommunities(List<PartnerNetworkOrganization> organizations, Specification<PartnerNetworkCommunity> specification, CriteriaBuilder cb) {
        var communityQuery = cb.createQuery(Tuple.class);
        var from = communityQuery.from(PartnerNetworkCommunity.class);
        var communityPath = from.get(PartnerNetworkCommunity_.community);

        communityQuery.multiselect(
                communityPath.get(Community_.id).alias(Community_.ID),
                communityPath.get(Community_.name).alias(Community_.NAME),
                communityPath.get(Community_.organizationId).alias(Community_.ORGANIZATION_ID)
        );
        communityQuery.where(specification.toPredicate(from, communityQuery, cb));

        var networkCommunities = entityManager.createQuery(communityQuery).getResultList();

        var grouped = networkCommunities.stream().collect(Collectors.groupingBy(t -> t.get(Community_.ORGANIZATION_ID, Long.class)));

        organizations.forEach(organization -> organization.setCommunities(
                grouped.get(organization.getId()).stream().map(this::convertCommunity).collect(Collectors.toList())
        ));

    }

    private DisplayableNamedEntity convertCommunity(Tuple communityTuple) {
        var result = new DisplayableNamedEntity();
        result.setId(communityTuple.get(Community_.ID, Long.class));
        result.setDisplayName(communityTuple.get(Community_.NAME, String.class));
        return result;
    }

}
