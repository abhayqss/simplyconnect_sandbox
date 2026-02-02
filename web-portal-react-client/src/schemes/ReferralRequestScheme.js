import { lazy, object } from "yup";
import { mapObject } from "underscore";

import { ALLOWED_FILE_FORMAT_MIME_TYPES, ALLOWED_FILE_FORMATS, ONLY_VIEW_ROLES } from "lib/Constants";

import { integer, ListOf, phoneNumber, Shape, string, stringMax } from "./types";

import { AddressScheme, EmailScheme, FileScheme } from "./";
import { getFileExtension } from "../lib/utils/FileUtils";

const { PDF, PNG, JPG, JPEG, GIF, TIFF, DOC, DOCX } = ALLOWED_FILE_FORMATS;

const ALLOWED_FILE_FORMAT_LIST = [PDF, PNG, JPG, JPEG, GIF, TIFF, DOC, DOCX];

const ALLOWED_FILE_MIME_TYPES = ALLOWED_FILE_FORMAT_LIST.map((type) => ALLOWED_FILE_FORMAT_MIME_TYPES[type]);
const user = JSON.parse(localStorage.getItem("AUTHENTICATED_USER") || "{}");

const isAssociationType = ONLY_VIEW_ROLES.includes(user?.roleName);
const isClientRequired = (scheme) => {
  return scheme.when(["$included"], (included, scheme) =>
    included.shouldValidateClientSection && !isAssociationType ? scheme.required() : scheme,
  );
};

const OrganizationCommunities = () => ListOf(integer()).required();

const ReferralRequestScheme = Shape({
  priorityId: integer().required(),
  person: string().required(),
  organizationPhone: phoneNumber().required(),
  organizationEmail: EmailScheme.required(),
  instructions: string().nullable().required(),
  services: ListOf(integer()).required().min(1),
  categoryType: ListOf(integer()).required(),
  referringCommunityId: integer().required(),
  referringOrganizationId: integer().required(),

  client: Shape({
    id: isClientRequired(integer()),
    address: AddressScheme.when(["$included"], (included, schema) =>
      included.shouldValidateClientSection && !isAssociationType ? schema : Shape(),
    ),
    location: isClientRequired(stringMax(256)),
    locationPhone: isClientRequired(phoneNumber()),
    insuranceNetworkTitle: isClientRequired(stringMax(256)),
  }),

  marketplace: Shape({
    sharedChannel: string().required(),
    sharedFax: phoneNumber().when("sharedChannel", (channel, schema) =>
      channel === "FAX" ? schema.required() : schema,
    ),
    sharedPhone: phoneNumber().when("sharedChannel", (channel, schema) =>
      channel === "FAX" ? schema.required() : schema,
    ),
  }).nullable(),

  attachmentFiles: ListOf(
    object().when((value) =>
      FileScheme({
        maxMB: 20,
        format: getFileExtension(value?.name),
        allowedTypes: ALLOWED_FILE_MIME_TYPES,
        allowedFormats: ALLOWED_FILE_FORMAT_LIST,
      }),
    ),
  ),

  attachedClientDocumentFiles: ListOf(
    object().when((value) =>
      FileScheme({
        maxMB: 20,
        format: getFileExtension(value?.name),
        allowedTypes: ALLOWED_FILE_MIME_TYPES,
        allowedFormats: ALLOWED_FILE_FORMAT_LIST,
      }),
    ),
  ),

  sharedCommunityIds: lazy((o) => Shape(mapObject(o, OrganizationCommunities))),

  vendorCareTeams: ListOf().when(["$included"], (included, scheme, { value }) =>
    ListOf(
      object().shape({
        contactId: integer().nullable().required(),
        teamType: string().nullable().required(),
        roleCode: string().nullable().required(),
      }),
    ),
  ),
});

export default ReferralRequestScheme;
