package com.scnsoft.eldermark.consana.sync.server.utils;

import com.scnsoft.eldermark.consana.sync.server.dao.CcdCodeDao;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Person;
import com.scnsoft.eldermark.consana.sync.server.model.entity.*;
import com.scnsoft.eldermark.consana.sync.server.model.enums.CodeSystem;
import com.scnsoft.eldermark.consana.sync.server.model.enums.IdentifierCode;
import com.scnsoft.eldermark.consana.sync.server.services.ConsanaUnknownCodeService;
import org.apache.commons.collections.CollectionUtils;
import org.hl7.fhir.instance.model.Address;
import org.hl7.fhir.instance.model.Medication;
import org.hl7.fhir.instance.model.*;
import org.hl7.fhir.instance.model.api.IBaseDatatype;
import org.jgroups.util.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.scnsoft.eldermark.consana.sync.server.constants.ConsanaSyncApiReceivePatientConstants.*;
import static com.scnsoft.eldermark.consana.sync.server.constants.FhirConstants.*;
import static com.scnsoft.eldermark.consana.sync.server.utils.ConsanaSyncServerUtils.*;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Service
@Transactional(noRollbackFor = Exception.class)
public class FhirConversionUtils {

    private static final Logger logger = LoggerFactory.getLogger(FhirConversionUtils.class);

    private final CcdCodeDao ccdCodeDao;
    private final ConsanaUnknownCodeService consanaUnknownCodeService;

    @Autowired
    public FhirConversionUtils(CcdCodeDao ccdCodeDao, ConsanaUnknownCodeService consanaUnknownCodeService) {
        this.ccdCodeDao = ccdCodeDao;
        this.consanaUnknownCodeService = consanaUnknownCodeService;
    }

    public CcdCode convertGender(Enumerations.AdministrativeGender administrativeGender) {
        if (administrativeGender == null) {
            return null;
        }
        switch (administrativeGender) {
            case MALE:
                return ccdCodeDao.getFirstByCodeAndCodeSystem("M", ADMINISTRATIVE_GENDER_CODE_SYSTEM);
            case FEMALE:
                return ccdCodeDao.getFirstByCodeAndCodeSystem("F", ADMINISTRATIVE_GENDER_CODE_SYSTEM);
            default:
                return null;
        }
    }

    public CcdCode convertMaritalStatus(CodeableConcept maritalStatus) {
        if (isEmpty(maritalStatus.getCoding())) {
            return null;
        }
        for (Coding coding : maritalStatus.getCoding()) {
            if (MARITAL_STATUS_CODE_SYSTEM.equals(coding.getSystem())) {
                return ccdCodeDao.getFirstByCodeAndCodeSystem(coding.getCode(), MARITAL_STATUS_CODE_SYSTEM);
            }
        }
        return null;
    }

    public CodeSystem getCodeSystem(Coding coding, String unknownSource) {
        CodeSystem codeSystem;
        //If coding.system is not provided - coding.system = SNOMED
        if (coding.getSystem() == null) {
            codeSystem = CodeSystem.SNOMED_CT;
        } else {
            codeSystem = CodeSystem.findBySystemUrl(coding.getSystem());
        }
        if (codeSystem == null) {
            var unknownCode = consanaUnknownCodeService.saveCode(new ConsanaUnknownCode(coding, unknownSource));
            logger.info("Added a new unknown code with id: {}", unknownCode.getId());
        }
        return codeSystem;
    }

    public static String fetchIdentifier(Patient patient, IdentifierCode code) {
        if (patient == null || isEmpty(patient.getIdentifier())) {
            return null;
        }
        for (Identifier identifier : patient.getIdentifier()) {
            if (identifier.getType() != null && isNotEmpty(identifier.getType().getCoding())) {
                for (Coding concept : identifier.getType().getCoding()) {
                    if (code.getCode().equals(concept.getCode()) && V2_IDENTIFIER_TYPE.equals(concept.getSystem())) {
                        return identifier.getValue();
                    }
                }
            }
        }
        return null;
    }

    public CcdCode convertRace(Patient patient) {
        if (patient == null || isEmpty(patient.getExtension())) {
            return null;
        }
        for (Extension extension : patient.getExtension()) {
            if (RACE_EXTENSION_URL.equals(extension.getUrl()) && (extension.getValue() instanceof CodeableConcept)) {
                var value = (CodeableConcept) extension.getValue();
                return ccdCodeDao.getFirstByValueSetAndDisplayName(RACE_VALUE_SET, value.getText());
            }
        }
        return null;
    }

    public CcdCode convertEthnicGroup(Patient patient) {
        return getCcdCode(patient, ETHNIC_GROUP_EXTENSION_URL, ETHNIC_GROUP_CODE_SYSTEM);
    }

    public CcdCode convertReligion(Patient patient) {
        return getCcdCode(patient, RELIGION_EXTENSION_URL, RELIGION_CODE_SYSTEM);
    }

    public static Instant getAdmitDate(Patient patient) {
        return ofNullable(findExtensionByUrl(patient.getExtension(), ADMIT_DATE_EXTENSION_URL))
                .map(Extension::getValue)
                .map(v -> v.castToDateTime(v).getValue().toInstant())
                .orElse(null);
    }

    public static Instant getDeathDate(Patient patient) {
        return ofNullable(patient.getDeceased())
                .map(date -> date.castToDateTime(date).getValue())
                .map(Date::toInstant)
                .orElse(null);
    }

    public static void updateResidentNames(Resident resident, Patient patient) {
        List<HumanName> humanNameList = patient.getName();
        if (isNotEmpty(humanNameList)) {
            HumanName humanName = humanNameList.get(0);
            String givenName = getStringFromListIfExists(humanName.getGiven());
            if (givenName != null) {
                String[] namesArr = givenName.split(" ");
                resident.setFirstName(namesArr[0]);
                if (namesArr.length > 1) {
                    resident.setMiddleName(namesArr[1]);
                }
            }
            resident.setLastName(getStringFromListIfExists(humanName.getFamily()));
        }
    }

    public static Person createOrUpdatePerson(Resident resident, Patient patient) {
        Person person = resident.getPerson();
        if (person == null) {
            person = new Person();
            person.setDatabase(resident.getDatabase());
            person.setLegacyTable(RBA_NAME_LEGACY_TABLE);
            person.setLegacyId(createLegacyId(LEGACY_ID_PREFIX, person));
        }
        createPersonName(person, resident, patient);
        createOrUpdatePersonAddress(person, patient);
        return person;
    }

    private static void createPersonName(Person person, Resident resident, Patient patient) {
        Name name = findNameWithLUseCode(person);
        if (name == null) {
            name = new Name();
            name.setNameUse(NAME_USE_CODE);
            name.setDatabase(person.getDatabase());
            name.setLegacyId(LEGACY_ID_PREFIX);
            name.setLegacyTable(RBA_NAME_LEGACY_TABLE);
            if (person.getNames() == null) {
                person.setNames(new ArrayList<>());
            }
            person.getNames().add(name);
            name.setPerson(person);
        }
        setLegacyIdFromParent(name, person);
        fillName(name, resident, patient);
    }

    private static Name findNameWithLUseCode(Person person) {
        List<Name> filteredNames = ofNullable(person.getNames())
                .map(names -> names.stream().filter(n -> NAME_USE_CODE.equals(n)).collect(toList()))
                .orElse(null);
        return isNotEmpty(filteredNames) ? filteredNames.get(0) : null;
    }

    private static void createOrUpdatePersonAddress(Person person, Patient patient) {
        if (person.getAddresses() == null) {
            person.setAddresses(new ArrayList<>());
        }

        PersonAddress personAddress;
        if (person.getAddresses().isEmpty()) {
            personAddress = new PersonAddress();

            personAddress.setDatabase(person.getDatabase());
            setLegacyIdFromParent(personAddress, person);
            personAddress.setLegacyTable(RBA_ADDRESS_LEGACY_TABLE);
            personAddress.setPerson(person);
            person.getAddresses().add(personAddress);
        } else {
            personAddress = person.getAddresses().get(0);
        }
        fillPersonAddress(personAddress, patient);
    }

    private static PersonAddress fillPersonAddress(PersonAddress personAddress, Patient patient) {
        Address address = getElementFromListIfExists(patient.getAddress());
        if (address != null) {
            personAddress.setStreetAddress(getStringFromListIfExists(address.getLine()));
            personAddress.setCity(address.getCity());
            personAddress.setState(address.getState());
            personAddress.setPostalCode(address.getPostalCode());
            personAddress.setCountry(address.getCountry());
        }
        return personAddress;
    }

    private static <T extends IBaseDatatype> T getElementFromListIfExists(List<T> objectList) {
        if (isEmpty(objectList)) {
            return null;
        }
        return objectList.stream()
                .filter(Objects::nonNull)
                .collect(toList())
                .get(0);
    }

    private static <T extends IBaseDatatype> String getStringFromListIfExists(List<T> objectList) {
        if (isEmpty(objectList)) {
            return null;
        }
        return objectList.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .findFirst()
                .orElse(null);
    }

    private static void fillName(Name name, Resident resident, Patient patient) {
        name.setGiven(resident.getFirstName());
        name.setFamily(resident.getLastName());
        name.setMiddle(resident.getMiddleName());
        name.setGivenNormalized(normalizeName(resident.getFirstName()));
        name.setFamilyNormalized(normalizeName(resident.getLastName()));
        List<HumanName> humanNameList = patient.getName();
        if (isNotEmpty(humanNameList)) {
            HumanName humanName = humanNameList.get(0);
            name.setPrefix(getStringFromListIfExists(humanName.getPrefix()));
            name.setSuffix(getStringFromListIfExists(humanName.getSuffix()));
        }
    }

    private CcdCode getCcdCode(Patient patient, String extensionUrl, String codeSystem) {
        return ofNullable(patient)
                .map(DomainResource::getExtension)
                .map(extList -> findExtensionByUrl(extList, extensionUrl))
                .map(extension -> {
                    var code = getExtensionCode(extension);
                    return ccdCodeDao.getFirstByCodeAndCodeSystem(code, codeSystem);
                })
                .orElse(null);
    }

    public static Extension findExtensionByUrl(List<Extension> extList, String extensionUrl) {
        if (extList == null) {
            return null;
        }
        return extList.stream()
                .filter(ext -> ext.getUrlElement() != null)
                .filter(ext -> extensionUrl.equals(ext.getUrlElement().getValue()))
                .findFirst()
                .orElse(null);
    }

    private String getExtensionCode(Extension extension) {
        return Stream.ofNullable(extension)
                .filter(Extension::hasValue)
                .map(Extension::getValue)
                .map(v -> v.getChildByName("code"))
                .filter(Property::hasValues)
                .map(Property::getValues)
                .flatMap(List::stream)
                .map(Object::toString)
                .findFirst()
                .orElse(null);
    }

    public static String getCitizenship(Patient patient) {
        return ofNullable(findExtensionByUrl(patient.getExtension(), CITIZENSHIP_URL))
                .map(Element::getExtension)
                .map(extList -> extList.stream().findFirst().orElse(null))
                .map(first -> ((CodeableConcept) first.getValue()).getText())
                .orElse(null);
    }

    public static BigDecimal getDoseQuantity(MedicationOrder order) {
        return Stream.ofNullable(order)
                .filter(MedicationOrder::hasDosageInstruction)
                .map(MedicationOrder::getDosageInstruction)
                .flatMap(List::stream)
                .filter(MedicationOrder.MedicationOrderDosageInstructionComponent::hasDose)
                .filter(MedicationOrder.MedicationOrderDosageInstructionComponent::hasDoseSimpleQuantity)
                .map(MedicationOrder.MedicationOrderDosageInstructionComponent::getDoseSimpleQuantity)
                .map(SimpleQuantity::getValue)
                .findFirst()
                .orElse(null);
    }

    public static String getDoseUnit(MedicationOrder order) {
        return ofNullable(findExtensionByUrl(order.getExtension(), DOSE_UNIT_EXTENSION_URL))
                .map(Extension::getValue)
                .map(Type::toString)
                .orElse(null);
    }

    public static Instant getInstant(Date date) {
        return ofNullable(date)
                .map(Date::toInstant)
                .orElse(null);
    }

    public static Tuple<Instant, Instant> getEffectiveDateRange(MedicationOrder.MedicationOrderDispenseRequestComponent dispense) {
        return Stream.ofNullable(dispense)
                .filter(MedicationOrder.MedicationOrderDispenseRequestComponent::hasValidityPeriod)
                .map(MedicationOrder.MedicationOrderDispenseRequestComponent::getValidityPeriod)
                .map(period -> {
                    Instant dateStart = getInstant(period.getStart());
                    Instant dateEnd = getInstant(period.getEnd());
                    return new Tuple<>(dateStart, dateEnd);
                })
                .findFirst()
                .orElse(new Tuple<>(null, null));
    }

    public static Integer getDispenseQuantity(MedicationOrder.MedicationOrderDispenseRequestComponent dispense) {
        return Stream.ofNullable(dispense)
                .filter(MedicationOrder.MedicationOrderDispenseRequestComponent::hasQuantity)
                .map(MedicationOrder.MedicationOrderDispenseRequestComponent::getQuantity)
                .map(Quantity::getValue)
                .map(BigDecimal::intValue)
                .findFirst()
                .orElse(null);
    }

    public static String getRxCui(Medication medication) {
        return ofNullable(findExtensionByUrl(medication.getExtension(), RX_CUI_EXTENSION_URL))
                .map(Extension::getValue)
                .map(Object::toString)
                .orElse(null);
    }

    public static <T> T getOrCreateCollectionElement(Collection<T> collection, int position, Supplier<T> supplier) {
        if (collection == null || position >= CollectionUtils.size(collection)) {
            return supplier.get();
        }
        return (T) CollectionUtils.get(collection, position);
    }

    public static <T, C extends Collection<T>> C updateCollection(C source, C target) {
        if (target != null) {
            target.clear();
            target.addAll(source);
            return target;
        }
        return source;
    }
}
