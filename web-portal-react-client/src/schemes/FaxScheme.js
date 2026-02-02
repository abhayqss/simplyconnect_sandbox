import { object } from "yup";

import validate from "validate.js";

import { Shape, string } from "./types";

import { ALLOWED_FILE_FORMAT_MIME_TYPES, ALLOWED_FILE_FORMATS, VALIDATION_ERROR_TEXTS } from "lib/Constants";
import { FileSchemeNullable } from "./FileScheme";
import { getFileExtension } from "../lib/utils/FileUtils";

const { FAX_FORMAT } = VALIDATION_ERROR_TEXTS;

const { DOC, DOCX, PDF, XLS, XLSX, TXT, JPEG, JPG, PNG, TIFF, GIF } = ALLOWED_FILE_FORMATS;

const { PATTERN: EMAIL_PATTERN } = validate.validators.email;

const ALLOWED_FILE_MIME_TYPES = [
  ALLOWED_FILE_FORMAT_MIME_TYPES[DOC],
  ALLOWED_FILE_FORMAT_MIME_TYPES[DOCX],
  ALLOWED_FILE_FORMAT_MIME_TYPES[XLS],
  ALLOWED_FILE_FORMAT_MIME_TYPES[XLSX],
  ALLOWED_FILE_FORMAT_MIME_TYPES[PDF],
  ALLOWED_FILE_FORMAT_MIME_TYPES[TIFF],
  ALLOWED_FILE_FORMAT_MIME_TYPES[JPG],
  ALLOWED_FILE_FORMAT_MIME_TYPES[PNG],
  ALLOWED_FILE_FORMAT_MIME_TYPES[GIF],
];
const ALLOWED_FILE_FORMAT_LIST = [DOC, DOCX, XLS, XLSX, PDF, TIFF, JPG, PNG, GIF];

const FaxScheme = Shape(
  {
    content: string().nullable().required(),
    recipientCategory: string().nullable().required(),
    header: string().nullable().required(),
    jobName: string().nullable(),
    contactId: string()
      .nullable()
      .when(["$included"], (included, scheme) => (included.isInternalType ? scheme.required() : scheme)),
    organizationId: string()
      .nullable()
      .when(["$included"], (included, scheme) => (included.isInternalType ? scheme.required() : scheme)),
    communityId: string()
      .nullable()
      .when(["$included"], (included, scheme) => (included.isInternalType ? scheme.required() : scheme)),
    recipientName: string()
      .nullable()
      .when(["$included"], (included, scheme) => (included.isInternalType ? scheme : scheme.required())),
    receiveFaxNumber: string()
      .nullable()
      .matches(/\+?(\d{10,16})?/, {
        message: FAX_FORMAT,
        excludeEmptyString: true,
      })
      .required(),
    file: object().when((value) =>
      FileSchemeNullable({
        maxMB: 20,
        format: getFileExtension(value?.name),
        allowedTypes: ALLOWED_FILE_MIME_TYPES,
        allowedFormats: ALLOWED_FILE_FORMAT_LIST,
      }).required(),
    ),
  },
  [[]],
);

export default FaxScheme;
