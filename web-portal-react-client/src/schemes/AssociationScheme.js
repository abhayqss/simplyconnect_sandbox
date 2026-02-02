import { bool, object } from "yup";

import validate from "validate.js";

import { integer, phoneNumber, Shape, string } from "./types";

import { ALLOWED_FILE_FORMAT_MIME_TYPES, ALLOWED_FILE_FORMATS, VALIDATION_ERROR_TEXTS } from "lib/Constants";
import FileScheme, { FileSchemeNullable } from "./FileScheme";
import { getFileExtension } from "../lib/utils/FileUtils";
import ZipCodeScheme from "./ZipCodeScheme";
import * as Yup from "yup";

const { EMAIL_FORMAT, NUMBER_FORMAT, ZIP_CODE_FORMAT } = VALIDATION_ERROR_TEXTS;

const { PATTERN: EMAIL_PATTERN } = validate.validators.email;

const { JPG, GIF, PNG, JPEG } = ALLOWED_FILE_FORMATS;

const ALLOWED_FILE_MIME_TYPES = [
  ALLOWED_FILE_FORMAT_MIME_TYPES[JPG],
  ALLOWED_FILE_FORMAT_MIME_TYPES[GIF],
  ALLOWED_FILE_FORMAT_MIME_TYPES[PNG],
  ALLOWED_FILE_FORMAT_MIME_TYPES[JPEG],
];
const URL_FORMAT = "Please enter a valid url";
const ALLOWED_FILE_FORMAT_LIST = [JPG, JPEG, GIF, PNG];

const AssociationScheme = Shape(
  {
    name: string().required(),
    website: string()
      .nullable()
      .matches(/(http(s)?:\/\/.)?(www\.)?[-a-zA-Z0-9@:%._+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_+.~#?&//=]*)/g, {
        message: URL_FORMAT,
        excludeEmptyString: true,
      })
      .optional()
      .required(),
    companyId: string().nullable().required(),
    street: string().nullable().required(),
    city: string().nullable().required(),
    state: Yup.mixed()
      .test(
        "is-string-or-number",
        "Please fill in the required field",
        (value) => (typeof value === "string" || typeof value === "number") && value.length !== 0,
      )
      .required(),
    zipCode: ZipCodeScheme().nullable().required(),
    logoPic: object().when((value) =>
      FileSchemeNullable({
        maxMB: 1,
        format: getFileExtension(value?.name),
        allowedTypes: ALLOWED_FILE_MIME_TYPES,
        allowedFormats: ALLOWED_FILE_FORMAT_LIST,
      }),
    ),

    phone: phoneNumber().nullable().required(),
    email: string()
      .nullable()
      .matches(EMAIL_PATTERN, {
        message: EMAIL_FORMAT,
        excludeEmptyString: true,
      })
      .required(),
  },
  [["medicaidNumber", "medicareNumber"]],
);

export default AssociationScheme;
