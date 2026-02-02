import { ASSESSMENT_TYPES } from "lib/Constants";

const { HMIS_ADULT_CHILD_INTAKE, HMIS_ADULT_CHILD_REASESSMENT, HMIS_ADULT_CHILD_REASESSMENT_EXIT } = ASSESSMENT_TYPES;

export default function prepareAssessmentData(data, type) {
  switch (type) {
    case HMIS_ADULT_CHILD_INTAKE:
      return prepareHMISAdultChildIntakeAssessmentData(data);
    case HMIS_ADULT_CHILD_REASESSMENT:
      return prepareHMISAdultChildReassessmentData(data);
    case HMIS_ADULT_CHILD_REASESSMENT_EXIT:
      return prepareHMISAdultChildQuestionnaireExitData(data);
    default:
      return {};
  }
}

function prepareHMISAdultChildIntakeAssessmentData(data) {
  return {
    // 'Case manager': data.programData?.caseManager,
    "Care manager": data.programData?.careManager,
    FM_SSN: data.individualMember?.ssnLastFourDigits,
    FM_DOB: data.individualMember?.birthDate,
    FM_QualityOfDOB: data.individualMember?.qualityOfDOB,
    FM_LastName: data.individualMember?.lastName,
    FM_FirstName: data.individualMember?.firstName,
    FM_MiddleName: data.individualMember?.middleName,
    FM_Alias: data.individualMember?.alias,
    FM_Suffix: data.individualMember?.suffix,
    FM_QualityOfName: data.individualMember?.qualityOfName,
    FM_GenderAssignedAtBirth: data.individualMember?.genderAssignedAtBirth,
    FM_SexualOrientation: data.individualMember?.sexualOrientation,
    FM_ZipCodeOfLastPermanentAddress: data.individualMember?.zipCodeOfLastPermanentAddress,
    FM_QualityOfZipCode: data.individualMember?.qualityOfZipCode,
    FM_PrimaryLanguage: data.individualMember?.primaryLanguage,
    "FM_USCitizen?": data.individualMember?.isUSCitizen,
    FM_Veteran: data.individualMember?.isVeteran,
    "FM_WorldWarIIOperations(1939-1945)": data.individualMember?.worldWarIIOperations,
    "FM_KoreanWarOperations(1950-1953)": data.individualMember?.koreanWarOperations,
    "FM_VietnamWarOperations(1961-1973)": data.individualMember?.vietnamWarOperations,
    "FM_PersianGulfWarOperations(1990-1991)": data.individualMember?.persianGulfWarOperations,
    "FM_AfghanistanOperations(2001-Present)": data.individualMember?.afghanistanWarOperations,
    "FM_Iraq(FreedomOperations(2003-2010)": data.individualMember?.iraqFreedomWarOperations,
    "FM_Iraq(NewDawnOperations)(2010-2011)": data.individualMember?.iraqNewDawnWarOperations,
    FM_OtherWarOperations: data.individualMember?.otherWarOperations,
    FM_MilitaryBranch: data.individualMember?.militaryBranch,
    FM_DischargeStatus: data.individualMember?.dischargeStatus,
    FM_DischargeStatusVerified: data.individualMember?.dischargeStatusVerified,
  };
}

function prepareHMISAdultChildReassessmentData(data) {
  const preparedData = {
    "Care manager": data.programData?.careManager,
    // 'Case manager': data.programData?.caseManager,
  };

  for (let memberIndex = 0; memberIndex < 6; memberIndex++) {
    const fieldNamePrefix = `FM_${memberIndex ? memberIndex + "_" : ""}`;

    Object.assign(preparedData, {
      [fieldNamePrefix + "SSN"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.demographics?.ssnLastFourDigits,
      [fieldNamePrefix + "DOB"]: data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.demographics?.birthDate,
      [fieldNamePrefix + "LastName"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.demographics?.lastName,
      [fieldNamePrefix + "FirstName"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.demographics?.firstName,
      [fieldNamePrefix + "MiddleName"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.demographics?.middleName,
      [fieldNamePrefix + "SexualOrientation"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.demographics?.sexualOrientation,
      [fieldNamePrefix + "StreetAddress"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.demographics?.streetAddress,
      [fieldNamePrefix + "City"]: data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.demographics?.city,
      [fieldNamePrefix + "ZipCode"]: data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.demographics?.zipCode,
      [fieldNamePrefix + "State"]: data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.demographics?.state,
      [fieldNamePrefix + "Phone"]: data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.demographics?.phone,
      [fieldNamePrefix + "Email"]: data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.demographics?.email,
      [fieldNamePrefix + "PermanentHousing"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.demographics?.isPermanentHousing,
      [fieldNamePrefix + "MoveInDate"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.demographics?.moveInDate,

      [fieldNamePrefix + "PhysicalDisability"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.hasPhysicalDisability,
      [fieldNamePrefix + "ReceivingServicesForPhysicalDisability"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability
          ?.isReceivingServicesForPhysicalDisability,
      [fieldNamePrefix + "LongTerm&Impairs"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.longTermImpairs,
      [fieldNamePrefix + "DevelopmentalDisability"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.developmentalDisability,
      [fieldNamePrefix + "ReceivingServicesForDevDisability"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.receivingServicesForDevDisability,
      [fieldNamePrefix + "ChronicHealthCondition"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.chronicHealthCondition,
      [fieldNamePrefix + "ReceivingServicesForCHC"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.receivingServicesForCHC,
      [fieldNamePrefix + "LongTerm&Impairs2"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.longTermImpairs2,
      [fieldNamePrefix + "HIV/AIDS"]: data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.hivAids,
      [fieldNamePrefix + "ReceivingServicesForHIV/AIDS"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.receivingServicesForHIVAIDS,
      [fieldNamePrefix + "MentalHealth"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.mentalHealth,
      [fieldNamePrefix + "ReceivingServicesForMH"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.receivingServicesForMH,
      [fieldNamePrefix + "LongTerm&ImpairsAbility"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.longTermImpairsAbility,
      [fieldNamePrefix + "SubstanceAbuse"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.substanceAbuse,
      [fieldNamePrefix + "ReceivingServicesForSA"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.receivingServicesForSA,
      [fieldNamePrefix + "LongTerm&ImpairsAbility2"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.longTermImpairsAbility2,
      [fieldNamePrefix + "DisablingCondition"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.disablingCondition,
      [fieldNamePrefix + "DomesticViolence"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.domesticViolence,
      [fieldNamePrefix + "LastDateOfDV"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.lastDateOfDV,
      [fieldNamePrefix + "AreYouCurrentlyFleeing?"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.areYouCurrentlyFleeing,
      [fieldNamePrefix + "Pregnant?"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.isPregnant,
      [fieldNamePrefix + "DueDate"]: data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.dueDate,

      [fieldNamePrefix + "CashIncome"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.cashIncome,
      [fieldNamePrefix + "SourceOfCashIncome"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.sourceOfCashIncome,
      [fieldNamePrefix + "EmploymentIncomeAmount"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.employmentIncomeAmount,
      [fieldNamePrefix + "UnemploymentInsAmount"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.unemploymentInsAmount,
      [fieldNamePrefix + "WorkersCompAmount"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.workersCompAmount,
      [fieldNamePrefix + "PrivateDisabilityInsAmount"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.privateDisabilityInsAmount,
      [fieldNamePrefix + "VADisabilityAmount"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.vaDisabilityAmount,
      [fieldNamePrefix + "SSDIAmount"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.ssdiAmount,
      [fieldNamePrefix + "SSIAmount"]: data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.ssiAmount,
      [fieldNamePrefix + "SSAAmount"]: data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.ssaAmount,
      [fieldNamePrefix + "VAPensionAmount"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.vaPensionAmount,
      [fieldNamePrefix + "PensionFromFormerFobAmount"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.pensionFromFormerFobAmount,
      [fieldNamePrefix + "TANFCalWorksAmount"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.tanfCalWorksAmount,
      [fieldNamePrefix + "GAAmount"]: data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.GAAmount,
      [fieldNamePrefix + "AlimonySpousalSupportAmount"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.alimonySpousalSupportAmount,
      [fieldNamePrefix + "ChildSupportAmount"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.childSupportAmount,
      [fieldNamePrefix + "OtherKidsAmount"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.otherKidsAmount,
      [fieldNamePrefix + "TotalAmount"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.totalAmount,
      [fieldNamePrefix + "NonCashBenefits"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.nonCashBenefits,
      [fieldNamePrefix + "SourcesOfNonCashBenefits"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.sourcesOfNonCashBenefits,
      [fieldNamePrefix + "OtherTANFBenefits"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.otherTANFBenefits,
      [fieldNamePrefix + "OtherExIHSSParatransitPG&ECareSchoolLunchProgramFoodVouchers"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.otherExIHSSParatransitPG,

      [fieldNamePrefix + "HealthInsurance"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.insurance?.healthInsurance,
      [fieldNamePrefix + "IfCoveredSourceOfHealthInsurance"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.insurance?.ifCoveredSourceOfHealthInsurance,
      [fieldNamePrefix + "OtherSourceOfHealthInsurance"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.insurance?.otherSourceOfHealthInsurance,
      [fieldNamePrefix + "Employed"]: data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.insurance?.employed,
      [fieldNamePrefix + "EmploymentTenure"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.insurance?.employmentTenure,
      [fieldNamePrefix + "HoursWorkedLastWeek"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.insurance?.hoursWorkedLastWeek,
      [fieldNamePrefix + "IfUnemployedSeeking"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.insurance?.ifUnemployedSeeking,

      [fieldNamePrefix + "SchoolCurrentlyEnrolled?"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.education?.schoolCurrentlyEnrolled,
      [fieldNamePrefix + "VocationalCurrentlyEnrolled?"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.education?.vocationalCurrentlyEnrolled,
      [fieldNamePrefix + "IfNotEnrolledLastDateOfEnrollment"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.education?.ifNotEnrolledLastDateOfEnrollment,
      [fieldNamePrefix + "HighestLevelOfSchoolCompletedORCurrentGradeEnrolled"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.education
          ?.highestLevelOfSchoolCompletedORCurrentGradeEnrolled,
      [fieldNamePrefix + "BarriersToEnrollingChildInSchool"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.education?.barriersToEnrollingChildInSchool,
      [fieldNamePrefix + "BarriersToEnrollingChildInSchoolOther"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.education?.barriersToEnrollingChildInSchoolOther,
      [fieldNamePrefix + "HighestDegreeEarned"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.education?.highestDegreeEarned,
      [fieldNamePrefix + "SchoolName"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.education?.schoolName,
      [fieldNamePrefix + "HUDHomelessLiaison"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.education?.hudHomelessLiaison,
      [fieldNamePrefix + "TypeOfSchool"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.education?.typeOfSchool,
    });
  }
  return preparedData;
}

function prepareHMISAdultChildQuestionnaireExitData(data) {
  const preparedData = {
    // 'Case manager': data.programData?.caseManager,
    "Care manager": data.programData?.careManager,
  };

  for (let memberIndex = 0; memberIndex < 6; memberIndex++) {
    const fieldNamePrefix = `FM_${memberIndex ? memberIndex + "_" : ""}`;

    Object.assign(preparedData, {
      [fieldNamePrefix + "SSN"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.demographics?.ssnLastFourDigits,
      [fieldNamePrefix + "LastName"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.demographics?.lastName,
      [fieldNamePrefix + "FirstName"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.demographics?.firstName,
      [fieldNamePrefix + "MiddleName"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.demographics?.middleName,

      [fieldNamePrefix + "PhysicalDisability"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.hasPhysicalDisability,
      [fieldNamePrefix + "ReceivingServicesForPhysicalDisability"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability
          ?.isReceivingServicesForPhysicalDisability,
      [fieldNamePrefix + "LongTerm&Impairs"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.longTermImpairs,
      [fieldNamePrefix + "DevelopmentalDisability"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.developmentalDisability,
      [fieldNamePrefix + "ReceivingServicesForDevDisability"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.receivingServicesForDevDisability,
      [fieldNamePrefix + "ChronicHealthCondition"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.chronicHealthCondition,
      [fieldNamePrefix + "ReceivingServicesForCHC"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.receivingServicesForCHC,
      [fieldNamePrefix + "LongTerm&Impairs2"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.longTermImpairs2,
      [fieldNamePrefix + "HIV/AIDS"]: data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.hivAids,
      [fieldNamePrefix + "ReceivingServicesForHIV/AIDS"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.receivingServicesForHIVAIDS,
      [fieldNamePrefix + "MentalHealth"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.mentalHealth,
      [fieldNamePrefix + "ReceivingServicesForMH"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.receivingServicesForMH,
      [fieldNamePrefix + "LongTerm&ImpairsAbility"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.longTermImpairsAbility,
      [fieldNamePrefix + "SubstanceAbuse"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.substanceAbuse,
      [fieldNamePrefix + "ReceivingServicesForSA"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.receivingServicesForSA,
      [fieldNamePrefix + "LongTerm&ImpairsAbility2"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.longTermImpairsAbility2,
      [fieldNamePrefix + "DisablingCondition"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.disablingCondition,
      [fieldNamePrefix + "DomesticViolence"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.domesticViolence,
      [fieldNamePrefix + "LastDateOfDV"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.lastDateOfDV,
      [fieldNamePrefix + "AreYouCurrentlyFleeing?"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.areYouCurrentlyFleeing,
      [fieldNamePrefix + "Pregnant?"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.isPregnant,
      [fieldNamePrefix + "DueDate"]: data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.disability?.dueDate,

      [fieldNamePrefix + "CashIncome"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.cashIncome,
      [fieldNamePrefix + "SourceOfCashIncome"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.sourceOfCashIncome,
      [fieldNamePrefix + "EmploymentIncomeAmount"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.employmentIncomeAmount,
      [fieldNamePrefix + "UnemploymentInsAmount"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.unemploymentInsAmount,
      [fieldNamePrefix + "WorkersCompAmount"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.workersCompAmount,
      [fieldNamePrefix + "PrivateDisabilityInsAmount"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.privateDisabilityInsAmount,
      [fieldNamePrefix + "VADisabilityAmount"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.vaDisabilityAmount,
      [fieldNamePrefix + "SSDIAmount"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.ssdiAmount,
      [fieldNamePrefix + "SSIAmount"]: data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.ssiAmount,
      [fieldNamePrefix + "SSAAmount"]: data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.ssaAmount,
      [fieldNamePrefix + "VAPensionAmount"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.vaPensionAmount,
      [fieldNamePrefix + "PensionFromFormerFobAmount"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.pensionFromFormerFobAmount,
      [fieldNamePrefix + "TANFCalWorksAmount"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.tanfCalWorksAmount,
      [fieldNamePrefix + "GAAmount"]: data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.GAAmount,
      [fieldNamePrefix + "AlimonySpousalSupportAmount"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.alimonySpousalSupportAmount,
      [fieldNamePrefix + "ChildSupportAmount"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.childSupportAmount,
      [fieldNamePrefix + "OtherKidsAmount"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.otherKidsAmount,
      [fieldNamePrefix + "TotalAmount"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.totalAmount,
      [fieldNamePrefix + "NonCashBenefits"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.nonCashBenefits,
      [fieldNamePrefix + "SourcesOfNonCashBenefits"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.sourcesOfNonCashBenefits,
      [fieldNamePrefix + "OtherTANFBenefits"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.otherTANFBenefits,
      [fieldNamePrefix + "OtherExIHSSParatransitPG&ECareSchoolLunchProgramFoodVouchers"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.benefits?.otherExIHSSParatransitPG,

      [fieldNamePrefix + "HealthInsurance"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.insurance?.healthInsurance,
      [fieldNamePrefix + "IfCoveredSourceOfHealthInsurance"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.insurance?.ifCoveredSourceOfHealthInsurance,
      [fieldNamePrefix + "OtherSourceOfHealthInsurance"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.insurance?.otherSourceOfHealthInsurance,
      [fieldNamePrefix + "Employed"]: data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.insurance?.employed,
      [fieldNamePrefix + "EmploymentTenure"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.insurance?.employmentTenure,
      [fieldNamePrefix + "HoursWorkedLastWeek"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.insurance?.hoursWorkedLastWeek,
      [fieldNamePrefix + "IfUnemployedSeeking"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.insurance?.ifUnemployedSeeking,

      [fieldNamePrefix + "SchoolCurrentlyEnrolled?"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.education?.schoolCurrentlyEnrolled,
      [fieldNamePrefix + "VocationalCurrentlyEnrolled?"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.education?.vocationalCurrentlyEnrolled,
      [fieldNamePrefix + "IfNotEnrolledLastDateOfEnrollment"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.education?.ifNotEnrolledLastDateOfEnrollment,
      [fieldNamePrefix + "HighestLevelOfSchoolCompletedORCurrentGradeEnrolled"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.education
          ?.highestLevelOfSchoolCompletedORCurrentGradeEnrolled,
      [fieldNamePrefix + "BarriersToEnrollingChildInSchool"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.education?.barriersToEnrollingChildInSchool,
      [fieldNamePrefix + "BarriersToEnrollingChildInSchoolOther"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.education?.barriersToEnrollingChildInSchoolOther,
      [fieldNamePrefix + "HighestDegreeEarned"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.education?.highestDegreeEarned,
      [fieldNamePrefix + "SchoolName"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.education?.schoolName,
      [fieldNamePrefix + "HUDHomelessLiaison"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.education?.hudHomelessLiaison,
      [fieldNamePrefix + "TypeOfSchool"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.education?.typeOfSchool,

      [fieldNamePrefix + "StreetAddress"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.forwardingAddress?.streetAddress,
      [fieldNamePrefix + "City"]: data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.forwardingAddress?.city,
      [fieldNamePrefix + "ZipCode"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.forwardingAddress?.zipCode,
      [fieldNamePrefix + "State"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.forwardingAddress?.state,
      [fieldNamePrefix + "Phone"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.forwardingAddress?.phone,
      [fieldNamePrefix + "Email"]:
        data?.[`familyMember${memberIndex > 0 ? memberIndex : ""}`]?.forwardingAddress?.email,
    });
  }

  return preparedData;
}
