import { bool, object } from "yup";

import validate from "validate.js";

import { integer, phoneNumber, Shape, string } from "./types";

import { ALLOWED_FILE_FORMAT_MIME_TYPES, ALLOWED_FILE_FORMATS, VALIDATION_ERROR_TEXTS } from "lib/Constants";
import { FileSchemeNullable } from "./FileScheme";
import { getFileExtension } from "../lib/utils/FileUtils";

const { PATTERN: EMAIL_PATTERN } = validate.validators.email;

const { JPG, GIF, PNG } = ALLOWED_FILE_FORMATS;

const ALLOWED_FILE_MIME_TYPES = [
  ALLOWED_FILE_FORMAT_MIME_TYPES[JPG],
  ALLOWED_FILE_FORMAT_MIME_TYPES[PNG],
  ALLOWED_FILE_FORMAT_MIME_TYPES[GIF],
];
const { EMAIL_FORMAT, NUMBER_FORMAT, ZIP_CODE_FORMAT } = VALIDATION_ERROR_TEXTS;

const ALLOWED_FILE_FORMAT_LIST = [JPG, GIF, PNG];

function CreateContactScheme(data) {
  const isUseVendorAddress = data.vendorAddressUsed;

  function requiredPart(scheme) {
    return scheme.when(["$included"], (_, scheme) => (isUseVendorAddress ? scheme : scheme.required()));
  }

  return Shape(
    {
      lastName: string().nullable().required(),
      firstName: string().nullable().required(),
      careTeamRoleCode: string().nullable().required(),
      login: string()
        .nullable()
        .matches(EMAIL_PATTERN, {
          message: EMAIL_FORMAT,
          excludeEmptyString: true,
        })
        .required(), //loginEmail

      mobilePhone: phoneNumber().nullable().required(),
      phone: phoneNumber().nullable(),

      avatar: object()
        .when((value) =>
          FileSchemeNullable({
            maxMB: 1,
            format: getFileExtension(value?.name),
            allowedTypes: ALLOWED_FILE_MIME_TYPES,
            allowedFormats: ALLOWED_FILE_FORMAT_LIST,
          }),
        )
        .required(),

      vendorAddressUsed: bool().nullable(),
      /*ssn: string().nullable().when(
                ['$included'],
                (included, scheme) => (
                    included.vendorAddressUsed
                        // eslint-disable-next-line no-template-curly-in-string
                        ? scheme.length(9, 'Please enter ${length} digits')
                            .matches(/^\d+$/, NUMBER_FORMAT)
                            .required()
                        : scheme
                )
            ),*/
      address: Shape({
        zip: string()
          .nullable()
          .matches(/^\d{5}$/, NUMBER_FORMAT)
          .length(5, ZIP_CODE_FORMAT)
          .required(),
        city: string().max(256).required(),
        street: string().max(256).required(),
        stateId: integer().required(),
      }),

      fax: string().nullable(),

      secureEmail: string().nullable().matches(EMAIL_PATTERN, {
        message: EMAIL_FORMAT,
        excludeEmptyString: true,
      }),
    },
    [["medicaidNumber", "medicareNumber"]],
  );
}

export default CreateContactScheme;
