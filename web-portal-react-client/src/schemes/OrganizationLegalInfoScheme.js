import { object } from "yup";

import { ALLOWED_FILE_FORMATS, ALLOWED_FILE_FORMAT_MIME_TYPES } from "lib/Constants";

import { Shape, string, integer, phoneNumber } from "./types";

import EmailScheme from "./EmailScheme";
import ZipCodeScheme from "./ZipCodeScheme";
import { FileSchemeNullable } from "./FileScheme";

import { getFileExtension } from "../lib/utils/FileUtils";

const { JPEG, JPG, PNG, GIF } = ALLOWED_FILE_FORMATS;

const ALLOWED_FILE_MIME_TYPES = [
  ALLOWED_FILE_FORMAT_MIME_TYPES[JPG],
  ALLOWED_FILE_FORMAT_MIME_TYPES[JPEG],
  ALLOWED_FILE_FORMAT_MIME_TYPES[PNG],
  ALLOWED_FILE_FORMAT_MIME_TYPES[GIF],
];

const ALLOWED_FILE_FORMAT_LIST = [JPG, JPEG, PNG, GIF];

const OrganizationLegalInfoScheme = Shape({
  name: string().min(3).required(),
  id: integer(),
  oid: string().when(["id"], (id, scheme) => {
    return id ? scheme : scheme.required();
  }),
  companyId: string().max(25).required(),
  email: EmailScheme.required(),
  phone: phoneNumber().required(),
  street: string().min(3).required(),
  city: string().required(),
  stateId: integer().required(),
  zipCode: ZipCodeScheme().required(),
  logo: object().when((value) =>
    FileSchemeNullable({
      maxMB: 1,
      format: getFileExtension(value?.name),
      allowedTypes: ALLOWED_FILE_MIME_TYPES,
      allowedFormats: ALLOWED_FILE_FORMAT_LIST,
    }),
  ),
});

export default OrganizationLegalInfoScheme;
