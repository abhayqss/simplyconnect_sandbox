import { array, bool, object } from "yup";

import { findIndex, isEqual, omit } from "underscore";

import validate from "validate.js";

import { integer, ListOf, phoneNumber, Shape, string } from "./types";

import { ALLOWED_FILE_FORMAT_MIME_TYPES, ALLOWED_FILE_FORMATS, VALIDATION_ERROR_TEXTS } from "lib/Constants";

import { map } from "lib/utils/ArrayUtils";

import Address from "./AddressScheme";
import { FileSchemeNullable } from "./FileScheme";
import { getFileExtension } from "../lib/utils/FileUtils";
import { isBlank, isEmpty, isNotBlank } from "../lib/utils/ObjectUtils";
import { List } from "immutable";

const { EMAIL_FORMAT, NUMBER_FORMAT, ZIP_CODE_FORMAT } = VALIDATION_ERROR_TEXTS;

const { PATTERN: EMAIL_PATTERN } = validate.validators.email;

const { JPG, GIF, PNG } = ALLOWED_FILE_FORMATS;

const ALLOWED_FILE_MIME_TYPES = [
  ALLOWED_FILE_FORMAT_MIME_TYPES[JPG],
  ALLOWED_FILE_FORMAT_MIME_TYPES[GIF],
  ALLOWED_FILE_FORMAT_MIME_TYPES[PNG],
];

const ALLOWED_FILE_FORMAT_LIST = [JPG, GIF, PNG];

// const MEDICARE_MEDICAID_NUMBER_ERROR_MESSAGE = "Medicare number or Medicaid number is required";
const MEDICARE_NUMBER_ERROR_MESSAGE = "Medicare number is required";
const MEDICAID_NUMBER_ERROR_MESSAGE = "Medicaid number is required";
const INACTIVE_CARE_TEAM_MEMMBER_ERROR_MESSAGE = "The selected care team member is inactive";
const INACTIVE_CHAT_METHOD_ERROR_MESSAGE = "The user has no longer access to Сhat feature";
const INACTIVE_EMAIL_METHOD_ERROR_MESSAGE = "The user doesn't have email address";

const ClientScheme = Shape(
  {
    lastName: string().nullable().required(),
    firstName: string().nullable().required(),
    ssn: string()
      .nullable()
      .when(["$included"], (included, scheme) =>
        included.shouldValidateSSN
          ? // eslint-disable-next-line no-template-curly-in-string
            scheme.length(9, "Please enter ${length} digits").matches(/^\d+$/, NUMBER_FORMAT).required()
          : scheme,
      ),
    hasNoSSN: bool().nullable(),
    birthDate: string().nullable().required(),
    languageWritten: array().of(string()).nullable(),
    languageSpoken: array().of(string()).nullable(),
    careTeamManager: string()
      .nullable()
      .when(["hieConsentPolicyName"], (hieConsentPolicyName, scheme) =>
        hieConsentPolicyName === "OPT_IN" ? scheme.required() : scheme,
      ),
    genderId: integer().required(),
    ethnicityId: integer(),
    address: Address,
    organizationId: integer().required(),
    communityId: integer().required(),
    phone: phoneNumber().nullable(),
    cellPhone: phoneNumber().nullable().required(),
    unit: string().nullable().max(12),
    hasNoEmail: bool().nullable(),
    email: string()
      .nullable()
      .matches(EMAIL_PATTERN, {
        message: EMAIL_FORMAT,
        excludeEmptyString: true,
      })
      .when(["hasNoEmail"], (hasNoEmail, scheme) => (hasNoEmail ? scheme : scheme.required())),
    avatar: object().when((value) =>
      FileSchemeNullable({
        maxMB: 1,
        format: getFileExtension(value?.name),
        allowedTypes: ALLOWED_FILE_MIME_TYPES,
        allowedFormats: ALLOWED_FILE_FORMAT_LIST,
      }),
    ),
    housingVouchers: ListOf().when(["$included"], (included, scheme, { value }) =>
      ListOf(
        object().shape({
          tCode: string()
            .nullable()
            .when(["$included"], (included, scheme) =>
              scheme.matches(/^\d{7}$/, "Must be a 7-digit number").required(),
            ),
          expiryDate: string().nullable(),
        }),
      ),
    ),
    attorneys: ListOf().when(["$included"], (included, scheme, { value }) =>
      ListOf(
        object().when((o) => {
          return Shape({
            firstName: string()
              .nullable()
              .when(["$included"], (included, scheme) =>
                isBlank(omit(o, "id")) && isEmpty(o.types) ? scheme.optional() : scheme.required(),
              ),
            lastName: string()
              .nullable()
              .when(["$included"], (included, scheme) =>
                isBlank(omit(o, "id")) && isEmpty(o.types) ? scheme.optional() : scheme.required(),
              ),
            types: ListOf()
              .nullable()
              .when(["$included"], (included, scheme) =>
                isBlank(omit(o, "id")) ? scheme.optional() : scheme.required(),
              ),
            email: string()
              .nullable()
              .matches(EMAIL_PATTERN, {
                message: EMAIL_FORMAT,
                excludeEmptyString: true,
              })
              .when(["$included"], (included, scheme) =>
                isBlank(omit(o, "id")) && isEmpty(o.types) ? scheme.optional() : scheme.optional(),
              ),
            phone: phoneNumber()
              .nullable()
              .when(["$included"], (included, scheme) =>
                isBlank(omit(o, "id")) && isEmpty(o.types) ? scheme.optional() : scheme.required(),
              ),
            street: string()
              .nullable()
              .when(["$included"], (included, scheme) =>
                isBlank(omit(o, "id")) && isEmpty(o.types) ? scheme.optional() : scheme.required(),
              ),
            city: string()
              .nullable()
              .when(["$included"], (included, scheme) =>
                isBlank(omit(o, "id")) && isEmpty(o.types) ? scheme.optional() : scheme.required(),
              ),
            state: integer()
              .nullable()
              .when(["$included"], (included, scheme) =>
                isBlank(omit(o, "id")) && isEmpty(o.types) ? scheme.optional() : scheme.required(),
              ),
            zipCode: string()
              .nullable()
              .matches(/^[0-9]{5}$/, {
                message: ZIP_CODE_FORMAT,
                excludeEmptyString: true,
              })
              .when(["$included"], (included, scheme) =>
                isBlank(omit(o, "id", "types", "stateTitle")) && isEmpty(o.types)
                  ? scheme.optional()
                  : scheme.required(),
              ),
          });
        }),
      ),
    ),
    contact: ListOf().when(["$included"], (included, scheme, { value }) =>
      ListOf(
        object().when((o) => {
          return Shape({
            type: string()
              .nullable()
              .when(["$included"], (included, scheme) =>
                isBlank(omit(o, "id", "type", "stateTitle")) ? scheme.optional() : scheme.required(),
              ),
            firstName: string()
              .nullable()
              .when(["$included"], (included, scheme) =>
                isBlank(omit(o, "id", "type", "stateTitle")) && isEmpty(o.type) ? scheme.optional() : scheme.required(),
              ),
            lastName: string()
              .nullable()
              .when(["$included"], (included, scheme) =>
                isBlank(omit(o, "id", "type", "stateTitle")) && isEmpty(o.type) ? scheme.optional() : scheme.required(),
              ),

            phone: phoneNumber()
              .nullable()
              .when(["$included"], (included, scheme) =>
                isBlank(omit(o, "id", "type", "stateTitle")) && isEmpty(o.type) ? scheme.optional() : scheme.required(),
              ),
            email: string().nullable().matches(EMAIL_PATTERN, {
              message: EMAIL_FORMAT,
              excludeEmptyString: true,
            }),
          });
        }),
      ),
    ),
    insurances: ListOf().when(["$included"], (included, scheme, { value }) =>
      value.length > 1
        ? ListOf(
            object().when((o) => {
              const groupNumbers = map(value, (o) => o.groupNumber);
              groupNumbers.splice(groupNumbers.indexOf(o.groupNumber), 1);

              const memberNumbers = map(value, (o) => o.memberNumber);
              memberNumbers.splice(memberNumbers.indexOf(o.memberNumber), 1);

              return Shape({
                groupNumber: string()
                  .nullable()
                  .when((_, scheme, { value }) =>
                    !value
                      ? scheme.optional()
                      : scheme.notOneOf(groupNumbers, "A duplicate number has been entered. Please review."),
                  ),
                memberNumber: string()
                  .nullable()
                  .when((_, scheme, { value }) =>
                    !value
                      ? scheme.optional()
                      : scheme.notOneOf(memberNumbers, "A duplicate number has been entered. Please review."),
                  ),
              });
            }),
          ).required()
        : scheme.optional(),
    ),
    insuranceAuthorizations: ListOf().when(["$included"], (included, scheme, { value }) =>
      included.hasInsuranceAuthorizations
        ? ListOf(
            object().when((o) => {
              const i = findIndex(value, (x) => isEqual(x, o));

              if (i === 0 || isNotBlank(omit(o, "index"))) {
                return Shape({
                  startDate: string().nullable().required(),
                  endDate: string().nullable().required(),
                  number: string().nullable().max(128).required(),
                });
              }

              return Shape({
                startDate: string().nullable(),
                endDate: string().nullable(),
                number: string().nullable().max(128),
              });
            }),
          ).required()
        : scheme.optional(),
    ),
    primaryContact: Shape({
      typeName: string().nullable().required(),
      careTeamMemberId: integer()
        .nullable()
        .when(["typeName", "$included"], (typeName, included, scheme) => {
          if (typeName === "SELF") return scheme;

          if (included.inactiveCareTeamMemberId)
            return scheme.notOneOf([included.inactiveCareTeamMemberId], INACTIVE_CARE_TEAM_MEMMBER_ERROR_MESSAGE);

          return scheme.required();
        }),
      notificationMethodName: string()
        .nullable()
        .required()
        .when(["$included"], (included, scheme) => {
          if (included.isChatInactive) return scheme.notOneOf(["CHAT"], INACTIVE_CHAT_METHOD_ERROR_MESSAGE);
          if (included.isEmailInactive) return scheme.notOneOf(["EMAIL"], INACTIVE_EMAIL_METHOD_ERROR_MESSAGE);

          return scheme;
        }),
    }),
    hieConsentPolicyName: string().nullable().required(),
    hieConsentPolicyObtainedFrom: string().nullable().required().max(520),
    /**
     * 1011
     */
    hasNoMedicareNumber: bool().nullable(),
    hasNoMedicaidNumber: bool().nullable(),
    medicaidNumber: string()
      .max(50)
      .nullable()
      .when(["$included"], (included, scheme) =>
        included.shouldValidateMedicaidNumber
          ? // eslint-disable-next-line no-template-curly-in-string
            scheme.required()
          : scheme,
      ),
    medicareNumber: string()
      .max(50)
      .nullable()
      .when(["$included"], (included, scheme) =>
        included.shouldValidateMedicareNumber
          ? // eslint-disable-next-line no-template-curly-in-string
            scheme.required()
          : scheme,
      ),

    primaryCarePhysicianPhone: phoneNumber().nullable(),
  },
  [["medicaidNumber", "medicareNumber", "hasNoMedicareNumber", "hasNoMedicaidNumber"]],
);

export default ClientScheme;
