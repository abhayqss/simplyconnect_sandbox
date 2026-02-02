
import validate from 'validate.js'

import {
  APPOINTMENT_STATUSES,
  VALIDATION_ERROR_TEXTS,
} from 'lib/Constants'

import {
  Shape,
  string,
  integer,
  ListOf
} from './types'

const { EMAIL_FORMAT } = VALIDATION_ERROR_TEXTS

const { PATTERN: EMAIL_PATTERN } = validate.validators.email

const COMPLETED_STATUS_IN_PAST_ERROR_TEXT = 'The appointment can\'t be completed before the appointment occurs'

const AppointmentScheme = Shape({
  title: string()
    .nullable()
    .max(256)
    .required(),
  status: string().nullable().required().when(
    ['$included'], (included, scheme) => (
      included.isEditing && !included.isDateFromInPast
        ? scheme.notOneOf([APPOINTMENT_STATUSES.COMPLETED], COMPLETED_STATUS_IN_PAST_ERROR_TEXT)
        : scheme
    )
  ),
  organizationId: integer().nullable().required(),
  communityId: integer().nullable().required(),
  location: string().nullable().max(256).required(),
  type: string().nullable().required(),
  serviceCategory: string().nullable().optional(),
  referralSource: string().nullable().max(256),
  reasonForVisit: string().nullable().max(5000).when(
    ['$included'], (included, scheme) => (
      included.isExternalProviderSelected ? scheme.required() : scheme.optional()
    )
  ),
  directionsInstructions: string().nullable().max(5000).optional(),
  notes: string().nullable().max(5000).optional(),

  clientId: integer().nullable().required(),
  creator: string().nullable().required(),
  date: integer().nullable().required(),
  from: string().nullable().required(),
  to: string().nullable().required(),

  reminders: ListOf().optional(),
  notificationMethods: ListOf().when(
      ['$included'], (included, scheme) => (
          !included.isNeverReminderSelected ? scheme.required() : scheme.optional()
      )
  ),
  email: string().nullable()
    .matches(EMAIL_PATTERN, {
      message: EMAIL_FORMAT,
      excludeEmptyString: true,
    })
    .when(
      ['$included'], (included, scheme) => (
        (!included.isNeverReminderSelected && included.isEmailNotificationSelected) ? scheme.required() : scheme.optional()
      )
    ),
  phone: string().nullable().when(
    ['$included'], (included, scheme) => (
      (!included.isNeverReminderSelected && included.isPhoneNotificationSelected) ? scheme.required() : scheme.optional()
    )
  )
})

export default AppointmentScheme
