import { filter, omit } from "underscore";

import { integer, ListOf, phoneNumber, Shape, string } from "./types";

import EmailScheme from "./EmailScheme";
import ZipCodeScheme from "./ZipCodeScheme";
import FileScheme, { FileSchemeNullable } from "./FileScheme";

import { ALLOWED_FILE_FORMAT_MIME_TYPES, ALLOWED_FILE_FORMATS, VALIDATION_ERROR_TEXTS } from "lib/Constants";
import { bool, object } from "yup";
import { getFileExtension } from "../lib/utils/FileUtils";

const { URL_FORMAT, EMPTY_FIELD } = VALIDATION_ERROR_TEXTS;

const { GIF, JPG, JPEG, PNG, CERT, CER, CRT } = ALLOWED_FILE_FORMATS;

const ALLOWED_FILE_FORMAT_LIST = [JPG, JPEG, PNG, GIF];

const ALLOWED_FILE_MIME_TYPES = [
  ALLOWED_FILE_FORMAT_MIME_TYPES[JPG],
  ALLOWED_FILE_FORMAT_MIME_TYPES[JPEG],
  ALLOWED_FILE_FORMAT_MIME_TYPES[PNG],
  ALLOWED_FILE_FORMAT_MIME_TYPES[GIF],
];

const ALLOWED_CERTIFICATE_MIME_TYPES = [
  ALLOWED_FILE_FORMAT_MIME_TYPES[CERT],
  ALLOWED_FILE_FORMAT_MIME_TYPES[CER],
  ALLOWED_FILE_FORMAT_MIME_TYPES[CRT],
];

function DocutrackPharmacyConfig({ businessUnitCodes }) {
  return Shape({
    serverDomain: string().nullable().required(),
    clientType: string().nullable().required(),
    publicKeyCertificates: ListOf(FileSchemeNullable({ maxMB: 50, allowedTypes: ALLOWED_CERTIFICATE_MIME_TYPES })).when(
      ["$included"],
      (included, scheme) => (included.useSuggestedCertificate ? scheme : scheme.min(1, EMPTY_FIELD)),
    ),
    businessUnitCodes: ListOf(
      string()
        .nullable()
        .test("is-uniq-if-not-empty", "Business unit code already exists. Please enter a unique code.", (value) => {
          const count = filter(businessUnitCodes, (code) => code === value)?.length;

          return !(value && count > 1);
        }),
    ),
  });
}

const CommunitySettingsScheme = Shape({
  id: integer(),
  name: string().min(3).required(),
  oid: string().when(["id"], (id, scheme) => {
    return id ? scheme : scheme.required();
  }),
  email: EmailScheme.required(),
  phone: phoneNumber().required(),
  logo: object().when((value) =>
    FileSchemeNullable({
      maxMB: 1,
      format: getFileExtension(value?.name),
      allowedTypes: ALLOWED_FILE_MIME_TYPES,
      allowedFormats: ALLOWED_FILE_FORMAT_LIST,
    }),
  ),
  cover: object().when((value) =>
    FileSchemeNullable({
      maxMB: 1,
      format: getFileExtension(value?.name),
      allowedTypes: ALLOWED_FILE_MIME_TYPES,
      allowedFormats: ALLOWED_FILE_FORMAT_LIST,
    }),
  ),
  pictureFiles: ListOf(
    object().when((value) =>
      FileScheme({
        maxMB: 20,
        format: getFileExtension(value?.name),
        allowedTypes: ALLOWED_FILE_MIME_TYPES,
        allowedFormats: ALLOWED_FILE_FORMAT_LIST,
      }),
    ),
  ),
  openFax: bool().nullable(),
  // fax: string()
  //   .nullable()
  //   .when(["openFax"], (openFax, scheme) => (openFax ? scheme.required() : scheme)),
  faxLogin: string()
    .nullable()
    .when(["openFax"], (openFax, scheme) => (openFax ? scheme.required() : scheme)),
  faxPassword: string()
    .nullable()
    .when(["openFax"], (openFax, scheme) => (openFax ? scheme.required() : scheme)),

  street: string().min(3).required(),
  city: string().required(),
  stateId: integer().required(),
  zipCode: ZipCodeScheme().required(),
  websiteUrl: string()
    .nullable()
    .matches(/(http(s)?:\/\/.)?(www\.)?[-a-zA-Z0-9@:%._+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_+.~#?&//=]*)/g, {
      message: URL_FORMAT,
      excludeEmptyString: true,
    })
    .optional(),

  docutrackPharmacyConfig: Shape().when(["$included"], (included, scheme, { value }) => {
    return included.shouldValidateDocutrack
      ? DocutrackPharmacyConfig({ businessUnitCodes: value.businessUnitCodes })
      : scheme;
  }),

  hieConsentPolicyName: string().nullable().required(),
});

export default CommunitySettingsScheme;
