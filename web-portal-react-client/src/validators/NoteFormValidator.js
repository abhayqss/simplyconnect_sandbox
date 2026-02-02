import BaseFormValidator from "./BaseFormValidator";

import { interpolate } from "lib/utils/Utils";
import { VALIDATION_ERROR_TEXTS } from "lib/Constants";

const { EMPTY_FIELD, LENGTH_MAXIMUM } = VALIDATION_ERROR_TEXTS;

const DEFAULT_CONSTRAINT = {
  presence: {
    allowEmpty: false,
    message: EMPTY_FIELD,
  },
};

function getConstrainByType(list, type, constrain = DEFAULT_CONSTRAINT) {
  return list.includes(type) ? constrain : null;
}

const getConstraintForSSCheck = (value, attributes, names, { included }) => {
  const { serviceStatusCheckSelected = false } = included;

  return serviceStatusCheckSelected ? DEFAULT_CONSTRAINT : null;
};

const getConstraintForCPCheck = (value, attributes, names, { included }) => {
  const { clientProgramSelected = false } = included;

  return clientProgramSelected ? DEFAULT_CONSTRAINT : null;
};

const CONSTRAINTS = {
  noteDate: DEFAULT_CONSTRAINT,
  subTypeId: DEFAULT_CONSTRAINT,
  admitDateId: (value, attributes, attributeName, { included }) => {
    const { subTypeId } = attributes;
    const { CMSubTypes = [] } = included;

    return getConstrainByType(CMSubTypes, subTypeId);
  },

  "encounter.typeId": (value, attributes, attributeName, { included }) => {
    const { subTypeId } = attributes;
    const { EncounterSubTypes = [] } = included;

    return getConstrainByType(EncounterSubTypes, subTypeId);
  },

  "encounter.clinicianId": (value, attributes, attributeName, { included }) => {
    const { subTypeId } = attributes;
    const { EncounterSubTypes = [] } = included;

    return getConstrainByType(EncounterSubTypes, subTypeId);
  },

  "encounter.otherClinician": (value, attributes, attributeName, { included }) => {
    const { subTypeId, encounter } = attributes;
    const { EncounterSubTypes = [] } = included;

    return EncounterSubTypes.includes(subTypeId) && encounter.clinicianId < 0
      ? {
          ...DEFAULT_CONSTRAINT,
          length: {
            minimum: 0,
            maximum: 256,
            tooLong: interpolate(LENGTH_MAXIMUM, 256),
          },
        }
      : {};
  },

  "encounter.toDate": (value, attributes, attributeName, { included }) => {
    const { subTypeId, encounter } = attributes;
    const { EncounterSubTypes = [] } = included;

    return getConstrainByType(EncounterSubTypes, subTypeId, {
      ...DEFAULT_CONSTRAINT,
      ...(value < encounter.fromDate && {
        inclusion: {
          message: "The date you entered occurs before the start date.",
        },
      }),
    });
  },

  "encounter.toTime": (value, attributes, attributeName, { included }) => {
    const { subTypeId, encounter } = attributes;
    const { EncounterSubTypes = [] } = included;

    return getConstrainByType(EncounterSubTypes, subTypeId, {
      ...DEFAULT_CONSTRAINT,
      ...(value < encounter.fromDate && {
        inclusion: {
          message: "The date you entered occurs before the start date.",
        },
      }),
    });
  },
  "encounter.fromDate": (value, attributes, attributeName, { included }) => {
    const { subTypeId } = attributes;
    const { EncounterSubTypes = [] } = included;

    return getConstrainByType(EncounterSubTypes, subTypeId);
  },
  subjective: {
    ...DEFAULT_CONSTRAINT,
    length: {
      minimum: 0,
      maximum: 20000,
      tooLong: interpolate(LENGTH_MAXIMUM, 20000),
    },
  },

  "serviceStatusCheck.servicePlanCreatedDate": getConstraintForSSCheck,
  "serviceStatusCheck.auditPerson": getConstraintForSSCheck,
  "serviceStatusCheck.checkDate": getConstraintForSSCheck,
  "serviceStatusCheck.serviceProvided": getConstraintForSSCheck,
  "serviceStatusCheck.resourceName": (value, attributes, names, { included }) => {
    const { serviceStatusCheckSelected = false } = included;
    const { providerName } = attributes.serviceStatusCheck;

    const shouldValidate = !providerName && serviceStatusCheckSelected;

    return shouldValidate ? DEFAULT_CONSTRAINT : null;
  },
  "serviceStatusCheck.providerName": (value, attributes, names, { included }) => {
    const { serviceStatusCheckSelected = false } = included;
    const { resourceName } = attributes.serviceStatusCheck;

    const shouldValidate = !resourceName && serviceStatusCheckSelected;

    return shouldValidate ? DEFAULT_CONSTRAINT : null;
  },

  "clientProgram.typeId": getConstraintForCPCheck,
  "clientProgram.serviceProvider": getConstraintForCPCheck,
  "clientProgram.startDate": getConstraintForCPCheck,
  "clientProgram.endDate": getConstraintForCPCheck,
};

class NoteFormValidator extends BaseFormValidator {
  validate(data, options) {
    return super.validate(data, CONSTRAINTS, { fullMessages: false, ...options });
  }
}

const validator = new NoteFormValidator();
export default validator;
