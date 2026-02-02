import { bool, object } from 'yup'

import {
	omit
} from 'underscore'

import validate from 'validate.js'

import {
	Shape,
	string,
	integer,
	phoneNumber
} from './types'

import {
	isNotEmptyOrBlank
} from 'lib/utils/ObjectUtils'

import {
	ALLOWED_FILE_FORMATS,
	VALIDATION_ERROR_TEXTS,
	ALLOWED_FILE_FORMAT_MIME_TYPES
} from 'lib/Constants'

import { getFileExtension } from 'lib/utils/FileUtils'

import Address from './AddressScheme'
import { FileSchemeNullable } from './FileScheme'

const {
	EMAIL_FORMAT,
	NUMBER_FORMAT,
	ZIP_CODE_FORMAT
} = VALIDATION_ERROR_TEXTS

const { PATTERN: EMAIL_PATTERN } = validate.validators.email

const { JPG, GIF, PNG } = ALLOWED_FILE_FORMATS

const ALLOWED_FILE_MIME_TYPES = [
	ALLOWED_FILE_FORMAT_MIME_TYPES[JPG],
	ALLOWED_FILE_FORMAT_MIME_TYPES[GIF],
	ALLOWED_FILE_FORMAT_MIME_TYPES[PNG]
]

const ALLOWED_FILE_FORMAT_LIST = [JPG, GIF, PNG]

function RelatedPartyScheme(data) {
	const hasRelatedParty = (
		isNotEmptyOrBlank(omit(data, 'address'))
		&& isNotEmptyOrBlank(data?.address)
	)

	function requiredIfHasRelatedParty(scheme) {
		return scheme.when(['$included'], (_, scheme) => hasRelatedParty ? scheme.required() : scheme)
	}

	return Shape({
		firstName: requiredIfHasRelatedParty(string().max(256).nullable()),
		lastName: requiredIfHasRelatedParty(string().max(256).nullable()),
		relationshipTypeName: requiredIfHasRelatedParty(string().nullable()),
		address: Shape({
			zip: requiredIfHasRelatedParty(string()
				.nullable()
				.matches(/^(\d{5})?$/, NUMBER_FORMAT)
				.matches(/^(.{5})?$/, ZIP_CODE_FORMAT)),
			city: requiredIfHasRelatedParty(string().max(256)),
			street: requiredIfHasRelatedParty(string().max(256)),
			stateId: requiredIfHasRelatedParty(integer())
		}),
		cellPhone: requiredIfHasRelatedParty(phoneNumber().nullable()),
		email: requiredIfHasRelatedParty(string().nullable().matches(EMAIL_PATTERN, {
			message: EMAIL_FORMAT,
			excludeEmptyString: true
		}))
	})
}

const ProspectScheme = Shape({
	lastName: string().max(256).nullable().required(),
	firstName: string().max(256).nullable().required(),
	middleName: string().max(256).nullable(),
	birthDate: string().nullable().required(),
	genderId: integer().nullable().required(),
	ssn: string().nullable().when(
		['$included'],
		(included, scheme) => (
			included.shouldValidateSSN
				// eslint-disable-next-line no-template-curly-in-string
				? scheme.length(9, 'Please enter ${length} digits')
					.matches(/^\d+$/, NUMBER_FORMAT)
					.required()
				: scheme
		)
	),
	hasNoSsn: bool().nullable(),
	avatar: object().when(value => (
			FileSchemeNullable({
				maxMB: 1,
				format: getFileExtension(value?.name),
				allowedTypes: ALLOWED_FILE_MIME_TYPES,
				allowedFormats: ALLOWED_FILE_FORMAT_LIST
			})
		)
	),
	insurancePaymentPlan: string().nullable().max(256),
	organizationId: integer().required(),
	communityId: integer().required(),
	address: Address,
	cellPhone: phoneNumber().nullable().required(),
	email: string().nullable().matches(EMAIL_PATTERN, {
		message: EMAIL_FORMAT,
		excludeEmptyString: true
	}),
	primaryContact: Shape({
		typeName: string().nullable().required(),
		notificationMethodName: string().nullable().required(),
	}),
	referralSource: string().nullable().max(256),
	notes: string().nullable().max(256),
	relatedParty: object().when(['$included'], (_, scheme, { value }) => RelatedPartyScheme(value)),
	secondOccupant: object().when(['$included'], (included, scheme) => {
		return !included.has2ndOccupant ? scheme.nullable() : Shape({
			lastName: string().max(256).nullable().required(),
			firstName: string().max(256).nullable().required(),
			middleName: string().max(256).nullable(),
			birthDate: string().nullable().required(),
			genderId: integer().nullable().required(),
			ssn: string().nullable().when(
				['$included'],
				(included, scheme) => (
					included.shouldValidateSSN
						// eslint-disable-next-line no-template-curly-in-string
						? scheme.length(9, 'Please enter ${length} digits')
							.matches(/^\d+$/, NUMBER_FORMAT)
							.required()
						: scheme
				)
			),
			hasNoSsn: bool().nullable(),
			avatar: object().when(value => (
					FileSchemeNullable({
						maxMB: 1,
						format: getFileExtension(value?.name),
						allowedTypes: ALLOWED_FILE_MIME_TYPES,
						allowedFormats: ALLOWED_FILE_FORMAT_LIST
					})
				)
			),
			address: Address,
			cellPhone: phoneNumber().nullable().required(),
			email: string().nullable().matches(EMAIL_PATTERN, {
				message: EMAIL_FORMAT,
				excludeEmptyString: true
			})
		})
	})
})

export default ProspectScheme
