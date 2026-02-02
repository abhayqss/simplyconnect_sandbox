package com.scnsoft.eldermark.util;


import com.scnsoft.eldermark.beans.projection.*;
import com.scnsoft.eldermark.entity.Name;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonAddress;
import com.scnsoft.eldermark.service.CareCoordinationConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.scnsoft.eldermark.service.CareCoordinationConstants.RBA_ADDRESS_LEGACY_TABLE;

public class CareCoordinationUtils {

    public static String getFullName(String firstName, String lastName) {
        return concat(" ", firstName, lastName);
    }

    public static String getFullName(String firstName, String middleName, String lastName) {
        return concat(" ", firstName, middleName, lastName);
    }

    public static String concat(String delimiter, String... args) {
        return Stream.of(args).filter(StringUtils::isNotEmpty).collect(Collectors.joining(delimiter));
    }

    public static String concat(String delimiter, Collection<String> collection) {
        return concat(delimiter, Stream.ofNullable(collection).flatMap(Collection::stream));
    }

    public static String concat(String delimiter, Stream<String> stream) {
        return stream.filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(delimiter));
    }

    public static String normalizeEmail(String str) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase();
    }

    public static String normalizeName(String str) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase().replaceAll("[' \\-]", "");
    }


    public static String normalizePhone(String str) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase().replaceAll("[' \\-+()]", "");
    }

    public static String deleteMultipleSpaces(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll("( )+", " ");
    }

    public static <T> List<T> nullOrSingletoneList(T elem) {
        if (elem == null) {
            return null;
        }
        return Collections.singletonList(elem);
    }

    public static boolean allNull(Object... obj) {
        return !ObjectUtils.anyNotNull(obj);
    }

    public static <T> Optional<T> getFistNotNull(Iterable<T> iterable) {
        if (iterable == null) {
            return Optional.empty();
        }
        for (T next : iterable) {
            if (next != null) {
                return Optional.of(next);
            }
        }
        return Optional.empty();
    }

    public static String truncate(String value, Integer length) {
        if (length != null && value != null && value.length() > length) {
            return value.substring(0, length);
        } else {
            return value;
        }
    }

    public static String trimAndRemoveMultipleSpaces(String str) {
        if (str == null) {
            return null;
        }
        return StringUtils.trim(str).replaceAll("\\s+", " ");
    }

    public static <I extends IdAware, C extends Collection<Long>> C toIds(Iterable<I> idAwares, Collector<Long, ?, C> collector) {
        return StreamUtils.stream(idAwares)
                .map(IdAware::getId)
                .collect(collector);
    }

    public static <I extends IdAware> Set<Long> toIdsSet(Iterable<I> idAwares) {
        return toIds(idAwares, Collectors.toSet());
    }

    public static <I extends IdAware> Set<Long> toIdsSet(Stream<I> idAwares) {
        return idAwares.map(IdAware::getId).collect(Collectors.toSet());
    }

    public static <I extends IdAware> Set<I> idsComparingSet() {
        return new TreeSet<>(Comparator.comparingLong(IdAware::getId));
    }

    public static <I extends IdAware, V> Map<I, V> idsComparingMap() {
        return new TreeMap<>(Comparator.comparingLong(IdAware::getId));
    }

    public static <T extends OrganizationIdAware> Stream<Long> getOrganizationIds(Collection<T> collection) {
        return CollectionUtils.emptyIfNull(collection).stream().map(OrganizationIdAware::getOrganizationId).filter(Objects::nonNull);
    }

    public static <T extends OrganizationIdAware> Set<Long> getOrganizationIdsSet(Collection<T> collection) {
        return getOrganizationIds(collection).collect(Collectors.toSet());
    }

    public static <T extends CommunityIdAware> Stream<Long> getCommunityIds(Collection<T> collection) {
        return CollectionUtils.emptyIfNull(collection).stream().map(CommunityIdAware::getCommunityId).filter(Objects::nonNull);
    }

    public static <T extends CommunityIdAware> Set<Long> getCommunityIdsSet(Collection<T> collection) {
        return getCommunityIds(collection).collect(Collectors.toSet());
    }

    public static <T> boolean isOnlyOneIsPresent(T... values) {
        return Stream.of(values).filter(Objects::nonNull).count() == 1;
    }

    public static <T extends AssociatedClientIdsAware> Set<Long> getAssociatedClientIds(Collection<T> employees) {
        return CollectionUtils.emptyIfNull(employees).stream()
                .map(AssociatedClientIdsAware::getAssociatedClientIds)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public static <T extends AssociatedEmployeeIdAware> Set<Long> getAssociatedEmployeeIds(Collection<T> clients) {
        return getAssociatedEmployeeIds(CollectionUtils.emptyIfNull(clients).stream());
    }

    public static <T extends AssociatedEmployeeIdAware> Set<Long> getAssociatedEmployeeIds(Stream<T> clients) {
        return clients
                .map(AssociatedEmployeeIdAware::getAssociatedEmployeeId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public static <T> List<T> wrapIfNonNull(T t) {
        return t == null ? Collections.emptyList() : Collections.singletonList(t);
    }

    public static boolean isEqualNullableCollection(Collection<?> a, Collection<?> b) {
        if (CollectionUtils.isEmpty(a) && CollectionUtils.isEmpty(b)) {
            return true;
        }
        if ((CollectionUtils.isEmpty(a) && CollectionUtils.isNotEmpty(b))
                || (CollectionUtils.isNotEmpty(a) && CollectionUtils.isEmpty(b))) {
            return false;
        }
        return CollectionUtils.isEqualCollection(a, b);
    }

    public static Person createNewPerson(Organization organization) {
        var target = createNewPersonNoLegacy(organization);

        com.scnsoft.eldermark.service.basic.CareCoordinationConstants.setLegacyId(target);
        target.setLegacyTable(com.scnsoft.eldermark.service.basic.CareCoordinationConstants.CCN_MANUAL_LEGACY_TABLE);

        return target;
    }

    public static Person createNewPerson(Organization organization, String legacyTable) {
        var target = createNewPersonNoLegacy(organization);

        com.scnsoft.eldermark.service.basic.CareCoordinationConstants.setLegacyId(target);
        target.setLegacyTable(legacyTable);

        return target;
    }

    private static Person createNewPersonNoLegacy(Organization organization) {
        var target = new Person();

        target.setOrganizationId(organization.getId());
        target.setOrganization(organization);

        return target;
    }

    public static Name createAndAddName(Person person, String firstName, String lastName) {
        return createAndAddName(person, firstName, lastName, CareCoordinationConstants.RBA_NAME_LEGACY_TABLE);
    }

    public static Name createAndAddName(Person person, String firstName, String lastName, String legacyTable) {
        var name = createAndAddNameNoLegacy(person, firstName, lastName);
        CareCoordinationConstants.setLegacyId(name);
        name.setLegacyTable(legacyTable);
        return name;
    }

    private static Name createAndAddNameNoLegacy(Person person, String firstName, String lastName) {
        var name = new Name();
        name.setNameUse("L");

        name.setOrganization(person.getOrganization());
        name.setOrganizationId(person.getOrganizationId());
        name.setPerson(person);

        if (person.getNames() == null) {
            person.setNames(new ArrayList<>());
        }

        person.getNames().add(name);

        fillName(name, firstName, lastName);

        return name;
    }

    public static void fillName(Name name, String firstName, String lastName) {
        name.setGiven(firstName);
        name.setGivenNormalized(CareCoordinationUtils.normalizeName(firstName));
        name.setFamily(lastName);
        name.setFamilyNormalized(CareCoordinationUtils.normalizeName(lastName));
    }

    public static <T, C extends Collection<T>> void putBidirectionally(Map<T, Set<T>> map,
                                                                       T employeeId1,
                                                                       T employeeId2) {
        putBidirectionally(map, employeeId1, employeeId2, HashSet::new);
    }

    public static <T, C extends Collection<T>> void putBidirectionally(Map<T, C> map,
                                                                       T employeeId1,
                                                                       T employeeId2,
                                                                       Supplier<C> collectionFactory
    ) {
        map.computeIfAbsent(employeeId1, e -> collectionFactory.get());
        map.computeIfAbsent(employeeId2, e -> collectionFactory.get());

        map.get(employeeId1).add(employeeId2);
        map.get(employeeId2).add(employeeId1);
    }

    public static PersonAddress createAddress() {
        var personAddress = new PersonAddress();
        personAddress.setLegacyTable(RBA_ADDRESS_LEGACY_TABLE);
        personAddress.setPostalAddressUse("HP");
        CareCoordinationConstants.setLegacyId(personAddress);
        return personAddress;
    }

    public static PersonAddress createAndAddAddress(Person person) {
        var personAddress = createAddress();
        if (person.getAddresses() == null) {
            person.setAddresses(new ArrayList<>());
        }

        person.getAddresses().add(personAddress);
        personAddress.setPerson(person);

        personAddress.setOrganization(person.getOrganization());
        personAddress.setOrganizationId(person.getOrganizationId());

        return personAddress;
    }

    public static <T> void setEmptyListIfNull(Supplier<List<T>> getter, Consumer<List<T>> setter) {
        if (getter.get() == null) {
            setter.accept(new ArrayList<>());
        }
    }

    public static <T> void setEmptyListIfNullAndClear(Supplier<List<T>> getter, Consumer<List<T>> setter) {
        var list = getter.get();
        if (list == null) {
            setter.accept(new ArrayList<>());
        } else {
            list.clear();
        }
    }

    public static <T> Set<T> setOfNullable(T t) {
        if (t == null) {
            return Set.of();
        }
        return Set.of(t);
    }
}
