package com.scnsoft.eldermark.dao.marketplace;

import com.scnsoft.eldermark.dao.StateDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.marketplace.*;
import com.scnsoft.eldermark.entity.phr.InNetworkInsurance;
import com.scnsoft.eldermark.shared.carecoordination.AddressDto;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class MarketplaceCustomDao {

    @PersistenceContext
    protected EntityManager entityManager;

    @Autowired
    private StateDao stateDao;

    public Page<Marketplace> filterMarketplaces(List<Long> careTypeIds, List<Long> communityTypeIds, Boolean isNotHavingServicesIncluded, List<Long> servicesTreatmentApproachesIds,
                                                List<Long> inNetworkInsurancesIds, List<Long> insurancePlansIds, Boolean emergencyServices, AddressDto address, String searchText, Pageable pageable, Double userLatitude, Double userLongitude){
        final TypedQuery<Marketplace> query = createMarketplacesQuery(careTypeIds, communityTypeIds, isNotHavingServicesIncluded, servicesTreatmentApproachesIds, inNetworkInsurancesIds, insurancePlansIds, emergencyServices, address, searchText, Boolean.FALSE, userLatitude, userLongitude);
        if (pageable != null) {
            query.setMaxResults(pageable.getPageSize());
            query.setFirstResult(pageable.getOffset());
        }
        final TypedQuery<Long> countQuery = createMarketplacesQuery(careTypeIds, communityTypeIds, isNotHavingServicesIncluded, servicesTreatmentApproachesIds, inNetworkInsurancesIds, insurancePlansIds, emergencyServices, address, searchText, Boolean.TRUE, userLatitude, userLongitude);

        return new PageImpl<Marketplace>(query.getResultList(), pageable, countQuery.getSingleResult());
    }

    public Pair<Long, Long> countMarketplaces(List<Long> careTypeIds, List<Long> communityTypeIds, Boolean isNotHavingServicesIncluded, List<Long> servicesTreatmentApproachesIds, List<Long> inNetworkInsurancesIds, List<Long> insurancePlansIds, Boolean emergencyServices, AddressDto address, String searchText) {
        Pair<Long, Long> result = new Pair<Long, Long>();
        TypedQuery<Long> countQuery = createMarketplacesQuery(careTypeIds, communityTypeIds, isNotHavingServicesIncluded, servicesTreatmentApproachesIds, inNetworkInsurancesIds, insurancePlansIds, emergencyServices, address, searchText, Boolean.TRUE, null, null);
        result.setFirst(countQuery.getSingleResult());
        TypedQuery<Long> totalCountQuery = createMarketplacesQuery(null, null, null,null, null, null, Boolean.FALSE, null, null, Boolean.TRUE, null, null);
        result.setSecond(totalCountQuery.getSingleResult());
        return result;
    }

    private TypedQuery createMarketplacesQuery(List<Long> primaryFocusIds, List<Long> communityTypeIds, Boolean isNotHavingServicesIncluded, List<Long> servicesTreatmentApproachesIds,
                                               List<Long> inNetworkInsurancesIds, List<Long> insurancePlansIds, Boolean emergencyServices, AddressDto address, String searchText, Boolean count, Double userLatitude, Double userLongitude) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery criteria;
        List<Predicate> predicates = new ArrayList<Predicate>();
        if (count) {
            criteria = cb.createQuery(Long.class);
        }  else if (!count && userLatitude != null && userLongitude != null) {
            return createQueryWithDistance(primaryFocusIds, communityTypeIds, isNotHavingServicesIncluded, servicesTreatmentApproachesIds,
                    inNetworkInsurancesIds, insurancePlansIds,  emergencyServices,  address,  searchText,   userLatitude,  userLongitude);
        } else {
            criteria = cb.createQuery(Marketplace.class);
        }
        final Root<Marketplace> root = criteria.from(Marketplace.class);
        if (count) {
            criteria.<Long>select(cb.countDistinct(root));
        }

        addMarketPlacePredicates(cb, predicates, root, primaryFocusIds, communityTypeIds, isNotHavingServicesIncluded, servicesTreatmentApproachesIds,
                inNetworkInsurancesIds, insurancePlansIds,  emergencyServices,  address,  searchText);

        criteria.distinct(true);

        criteria.where(predicates.toArray(new Predicate[]{}));

        return entityManager.createQuery(criteria);
    }

    private void addMarketPlacePredicates(CriteriaBuilder cb, List<Predicate> predicates, Root<Marketplace> root, List<Long> primaryFocusIds, List<Long> communityTypeIds, Boolean isNotHavingServicesIncluded, List<Long> servicesTreatmentApproachesIds, List<Long> inNetworkInsurancesIds, List<Long> insurancePlansIds, Boolean emergencyServices, AddressDto address, String searchText) {
        if (!CollectionUtils.isEmpty(primaryFocusIds)) {
            Join<Marketplace, PrimaryFocus> joinPrimaryFocuses = root.join("primaryFocuses");
//            predicates.add(cb.equal(joinPrimaryFocuses.<Long>get("id"), primaryFocusId));
            Expression<Long> joinExpression = joinPrimaryFocuses.get("id");
            predicates.add(joinExpression.in(primaryFocusIds));
        }

        if (!CollectionUtils.isEmpty(communityTypeIds)) {
            Join<Marketplace, CommunityType> joinCommunityTypes = root.join("communityTypes");
            Expression<Long> joinExpression = joinCommunityTypes.get("id");
            predicates.add(joinExpression.in(communityTypeIds));
        }

        if (!CollectionUtils.isEmpty(servicesTreatmentApproachesIds) && (isNotHavingServicesIncluded != null && BooleanUtils.isTrue(isNotHavingServicesIncluded))) {
            Join<Marketplace, ServicesTreatmentApproach> join = root.join("servicesTreatmentApproaches", JoinType.LEFT);
            Expression<Long> joinExpression = join.get("id");
            Predicate serviceIds = joinExpression.in(servicesTreatmentApproachesIds);
            Expression<Collection<ServicesTreatmentApproach>> services = root.get("servicesTreatmentApproaches");
            Predicate emptyServices = cb.isEmpty(services);
            predicates.add(cb.or(serviceIds, emptyServices));
        } else if (!CollectionUtils.isEmpty(servicesTreatmentApproachesIds)) {
            Join<Marketplace, ServicesTreatmentApproach> join = root.join("servicesTreatmentApproaches");
            Expression<Long> joinExpression = join.get("id");
            predicates.add(joinExpression.in(servicesTreatmentApproachesIds));
        } else  if (isNotHavingServicesIncluded != null && BooleanUtils.isTrue(isNotHavingServicesIncluded)) {
            Expression<Collection<ServicesTreatmentApproach>> services = root.get("servicesTreatmentApproaches");
            predicates.add(cb.isEmpty(services));
        }

        if (!CollectionUtils.isEmpty(inNetworkInsurancesIds)) {
            Join<Marketplace, InNetworkInsurance> join = root.join("inNetworkInsurances");
            Expression<Long> joinExpression = join.get("id");
            predicates.add(joinExpression.in(inNetworkInsurancesIds));
        }

        if (!CollectionUtils.isEmpty(insurancePlansIds)) {
            Join<Marketplace, InsurancePlan> join = root.join("insurancePlans");
            Expression<Long> joinExpression = join.get("id");
            predicates.add(joinExpression.in(insurancePlansIds));
        }

        if (BooleanUtils.isTrue(emergencyServices)) {
            predicates.add(cb.isNotEmpty(root.<List>get("emergencyServices")));
        }

        predicates.add(cb.equal(root.<Boolean>get("discoverable"), Boolean.TRUE));
        predicates.add(cb.isNotNull(root.get("organization")));

        if (address != null) {
            final Join<Marketplace, Organization> joinOrganization = root.join("organization");
            final Join<Organization, OrganizationAddress> joinOrgAddress = joinOrganization.join("addresses");
            if (address.getState() != null) {
                predicates.add(cb.equal(joinOrgAddress.<String>get("state"), address.getState().getLabel()));
            }
            if (address.getCity() != null) {
                predicates.add(cb.equal(joinOrgAddress.<String>get("city"), address.getCity()));
            }
            if (address.getZip() != null) {
                predicates.add(cb.equal(joinOrgAddress.<String>get("postalCode"), address.getZip()));
            }
        }

        if (StringUtils.isNotBlank(searchText)) {
            final Join<Marketplace, Organization> joinOrganization = root.join("organization");
            final Join<Organization, OrganizationAddress> joinOrgAddress = joinOrganization.join("addresses");
            Join<Marketplace, Database> joinDatabase = root.join("database");
            Join<Marketplace, CommunityType> joinCommunityTypes = root.join("communityTypes", JoinType.LEFT);
            String likeFormatSearchStr = String.format("%%%s%%", searchText);

            List<State> states = stateDao.searchByFullNameLike(searchText);
            List<Predicate> statePredicates = new ArrayList<Predicate>();
            statePredicates.add(cb.like(joinOrgAddress.<String>get("state"), likeFormatSearchStr));
            if (!CollectionUtils.isEmpty(states)) {
                for (State stateEntity : states) {
                    statePredicates.add(cb.like(joinOrgAddress.<String>get("state"), stateEntity.getAbbr()));
                }
            }
            Predicate state = cb.or(statePredicates.toArray(new Predicate[]{}));

            Predicate city = cb.like(joinOrgAddress.<String>get("city"), likeFormatSearchStr);
            Predicate zip = cb.like(joinOrgAddress.<String>get("postalCode"), likeFormatSearchStr);
            Predicate streetAddress = cb.like(joinOrgAddress.<String>get("streetAddress"), likeFormatSearchStr);
            Predicate communityName = cb.like(joinOrganization.<String>get("name"), likeFormatSearchStr);
            Predicate databaseName = cb.like(joinDatabase.<String>get("name"), likeFormatSearchStr);
            Predicate communityTypeNames = cb.like(joinCommunityTypes.<String>get("displayName"), likeFormatSearchStr);
            predicates.add(cb.or(state, city, zip, streetAddress, communityName, databaseName, communityTypeNames));
        }

    }

    private TypedQuery createQueryWithDistance(List<Long> primaryFocusIds, List<Long> communityTypeIds, Boolean isNotHavingServicesIncluded, List<Long> servicesTreatmentApproachesIds, List<Long> inNetworkInsurancesIds, List<Long> insurancePlansIds, Boolean emergencyServices, AddressDto address, String searchText, Double userLatitude, Double userLongitude) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery criteria = cb.createQuery(Marketplace.class);
        final Root<Marketplace> root = criteria.from(Marketplace.class);

        final Join<Marketplace, Organization> joinOrganization = root.join("organization");
        final Join<Organization, OrganizationAddress> joinOrgAddress = joinOrganization.join("addresses");

        final Subquery<Marketplace> subquery = criteria.subquery(Marketplace.class);
        final Root<Marketplace> subqueryRoot = subquery.from(Marketplace.class);
        subquery.select(subqueryRoot);

        final List<Predicate> subQueryPredicates = new ArrayList<>();
        addMarketPlacePredicates(cb, subQueryPredicates, subqueryRoot, primaryFocusIds, communityTypeIds, isNotHavingServicesIncluded, servicesTreatmentApproachesIds, inNetworkInsurancesIds,
                insurancePlansIds, emergencyServices,  address, searchText);
        subquery.where(subQueryPredicates.toArray(new Predicate[]{}));

        criteria.distinct(false);
        criteria.where(root.in(subquery));

        final List<Order> orderList = new ArrayList<>();
        final Expression<Double> distanceFunction = cb.function("dbo.GreatCircleDistanceAngleGrad",
                Double.class, cb.literal(userLongitude), cb.literal(userLatitude), joinOrgAddress.get("longitude"), joinOrgAddress.get("latitude")
        );

        orderList.add(cb.asc(distanceFunction));
        criteria.orderBy(orderList);

        return entityManager.createQuery(criteria);
    }

    public List<Marketplace> getMarketpaces(List<Long> ids) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final TypedQuery<Marketplace> query = entityManager.createQuery("Select m from Marketplace m WHERE m.id in :ids", Marketplace.class);
        query.setParameter("ids", ids);
        return query.getResultList();
    }
}

