import * as Yup from "yup";
import { array, boolean, number, object } from "yup";
import validate from "validate.js";

import { integer, ListOf, phoneNumber, Shape, string } from "./types";

import { ALLOWED_FILE_FORMAT_MIME_TYPES, ALLOWED_FILE_FORMATS, VALIDATION_ERROR_TEXTS } from "lib/Constants";
import FileScheme, { FileSchemeNullable } from "./FileScheme";
import { getFileExtension } from "../lib/utils/FileUtils";
import ZipCodeScheme from "./ZipCodeScheme";

const { EMAIL_FORMAT, NUMBER_FORMAT, ZIP_CODE_FORMAT } = VALIDATION_ERROR_TEXTS;

const { PATTERN: EMAIL_PATTERN } = validate.validators.email;

const { JPG, GIF, PNG, JPEG } = ALLOWED_FILE_FORMATS;

const ALLOWED_FILE_MIME_TYPES = [
  ALLOWED_FILE_FORMAT_MIME_TYPES[JPG],
  ALLOWED_FILE_FORMAT_MIME_TYPES[PNG],
  ALLOWED_FILE_FORMAT_MIME_TYPES[GIF],
  ALLOWED_FILE_FORMAT_MIME_TYPES[JPEG],
];

const ALLOWED_FILE_FORMAT_LIST = [JPG, GIF, PNG, JPEG];

const URL_FORMAT = "Please enter a valid url";
const VendorScheme = Shape(
  {
    name: string().nullable().required(),
    companyId: string().nullable().required(),

    phone: phoneNumber().nullable(),

    premium: string().nullable().required(),
    hieAgreement: boolean().nullable().required(),
    companyTypeId: integer().nullable(),
    clinicalVendor: boolean().nullable().required(),

    license: string().nullable(),
    otherLicense: string().nullable(),
    // address
    zipCode: ZipCodeScheme().nullable().required(),
    street: string().nullable().min(3).required(),
    city: string().nullable().required(),
    state: Yup.mixed()
      .test(
        "is-string-or-number",
        "Please fill in the required field",
        (value) => (typeof value === "string" || typeof value === "number") && value.length !== 0,
      )
      .required(),

    website: string()
      .nullable()
      .matches(/(http(s)?:\/\/.)?(www\.)?[-a-zA-Z0-9@:%._+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_+.~#?&//=]*)/g, {
        message: URL_FORMAT,
        excludeEmptyString: true,
      })
      .optional(),

    credential: string().nullable(),
    expYear: string().nullable().matches(/^\d+$/, NUMBER_FORMAT),
    oid: string().nullable().required(),
    cms: string().nullable(),
    vendorTypeIds: array().of(string()).nullable().required(),
    serviceIds: array().of(string()).nullable().required(),
    languageIds: array().of(string()),
    // .min(1)
    // .required() ,
    email: string()
      .nullable()
      .matches(EMAIL_PATTERN, {
        message: EMAIL_FORMAT,
        excludeEmptyString: true,
      })
      .required(),

    introduction: string().nullable().max(1024),

    /**
     * business hours
     */
    operatingWorkDay: string().nullable(),
    operatingSaturday: string().nullable(),
    operatingSunday: string().nullable(),

    logoPic: object().when((value) =>
      FileSchemeNullable({
        maxMB: 1,
        format: getFileExtension(value?.name),
        allowedTypes: ALLOWED_FILE_MIME_TYPES,
        allowedFormats: ALLOWED_FILE_FORMAT_LIST,
      }),
    ),
    vendorPhotos: ListOf(
      object().when((value) => {
        return FileScheme({
          maxMB: 1,
          format: getFileExtension(value?.name),
          allowedTypes: ALLOWED_FILE_MIME_TYPES,
          allowedFormats: ALLOWED_FILE_FORMAT_LIST,
        });
      }),
    ).max(4, "Maximum of 4 photos"),
  },
  [["medicaidNumber", "medicareNumber"]],
);

export default VendorScheme;
