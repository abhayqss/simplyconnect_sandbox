import {
    Shape,
    string,
    integer
} from './types'

import { VALIDATION_ERROR_TEXTS } from 'lib/Constants'

import { interpolate } from 'lib/utils/Utils'

const {
    NUMBER_FORMAT,
    ZIP_CODE_FORMAT,
    NUMBER_FORMAT_SPECIFIC
} = VALIDATION_ERROR_TEXTS

const ClientRecordFilterScheme = Shape({
    lastName: string().nullable().required(),
    firstName: string().nullable().required(),
    genderId: integer().required(),
    birthDate: string().nullable().required(),
    ssnLast4: string()
        .nullable()
        .when("$included", (included, scheme) => (
            !included.canRequestAccess
                ? scheme
                    .matches(/^[0-9]{4}$/, NUMBER_FORMAT)
                    .length(4, interpolate(NUMBER_FORMAT_SPECIFIC, 4))
                    .required()
                : scheme.optional()
        )),
    zip: string()
        .nullable()
        .matches(/^(\d{5})?$/, NUMBER_FORMAT)
        .matches(/^(.{5})?$/, ZIP_CODE_FORMAT)
})

export default ClientRecordFilterScheme
