import { bool, lazy, object } from 'yup'

import { mapObject } from 'underscore'

import {
    ALLOWED_FILE_FORMATS,
    VALIDATION_ERROR_TEXTS,
    ALLOWED_FILE_FORMAT_MIME_TYPES,
} from 'lib/Constants'

import { Shape, ListOf, string, integer, phoneNumber } from './types'

import FileScheme from './FileScheme'
import EmailScheme from './EmailScheme'
import { getFileExtension } from '../lib/utils/FileUtils'

const { isImmutable } = require('immutable')

const { EMPTY_FIELD } = VALIDATION_ERROR_TEXTS

const { PDF, PNG, JPG, JPEG, GIF, TIFF } = ALLOWED_FILE_FORMATS

const ALLOWED_FILE_FORMAT_LIST = [PDF, PNG, JPG, JPEG, GIF, TIFF]

const ALLOWED_FILE_MIME_TYPES = ALLOWED_FILE_FORMAT_LIST.map(type => ALLOWED_FILE_FORMAT_MIME_TYPES[type])

function toJS(o) {
    return isImmutable(o) ? o.toJS() : o
}

const TypeSchemeMap = {
    string: string(),
    boolean: bool().nullable(),
    object: integer(),
    number: integer(),
}

const requireIfNotDraft = scheme => scheme.when(
    ['$included'],
    (included, schema) => included.isDraft ? schema : schema.required()
)

/*const NotifiedPerson = Shape({
    name: string().required(),
    notificationChannels: ListOf().min(1, EMPTY_FIELD),
    email: string().when(
        ['notificationChannels'],
        (notificationChannels, scheme) => (
            notificationChannels.includes('EMAIL') ? EmailScheme.required() : scheme
        )
    ),
    phone: string().when(
        ['notificationChannels'],
        (notificationChannels, scheme) => (
            notificationChannels.includes('PHONE') ||
            notificationChannels.includes('FAX')
                ? phoneNumber().required() : scheme
        )
    ),
    notifiedDate: integer().required(),
})*/

const PersonWithPhone = () => Shape({
    phone: phoneNumber(),
})

const Notification = () => lazy(o => Shape(
    mapObject(toJS(o), (value, key) => {
        let scheme = TypeSchemeMap[typeof value]
       
        if (key === 'phone') {
            scheme = phoneNumber()
        }

        if (o.isNotified) {
            scheme = scheme.required()
        }

        return scheme
    })
))

export default Shape({
    client: Shape({
        unit: string().required(),
        phone: phoneNumber().required(),
        siteName: string().required(),
        address: string().required(),
    }),
    incidentDate: requireIfNotDraft(string().nullable()),
    incidentDiscoveredDate: requireIfNotDraft(string().nullable()),
    wasProviderPresentOrScheduled: requireIfNotDraft(bool().nullable()),
    places: ListOf().when(
        ['$included'],
        (included, schema) => included.isDraft ? schema : schema.min(1, EMPTY_FIELD)
    ),
    wereApparentInjuries: bool().nullable(),
    injuries: ListOf().when(
        ['wereApparentInjuries'],
        (wereApparentInjuries, scheme) => wereApparentInjuries ? scheme.min(1, 'You have not specified that there were injuries') : scheme
    ),
    currentInjuredClientCondition: string().when(
        ['wereApparentInjuries'],
        (wereApparentInjuries, scheme) => wereApparentInjuries ? scheme.required() : scheme
    ),
    witnesses: ListOf(PersonWithPhone()),
    involvedIndividuals: ListOf(PersonWithPhone()),
    incidentPictureFiles: ListOf(
        object().when(value => (
                FileScheme({
                    maxMB: 20,
                    format: getFileExtension(value?.name),
                    allowedTypes: ALLOWED_FILE_MIME_TYPES,
                    allowedFormats: ALLOWED_FILE_FORMAT_LIST
                })
            )
        )
    ),
    // notifiedPersons: ListOf(NotifiedPerson),
    notification: lazy(o => Shape(mapObject(toJS(o), Notification))),
    immediateIntervention: requireIfNotDraft(string()),
    followUpInformation: requireIfNotDraft(string()),
    completedBy: string().required(),
    completedByPosition: string().required(),
    completedByPhone: requireIfNotDraft(phoneNumber()),
    completedDate: integer().required(),
    reportedBy: requireIfNotDraft(string()),
    reportedByPosition: requireIfNotDraft(string()),
    reportedByPhone: requireIfNotDraft(phoneNumber()),
    reportDate: integer().required(),
})
