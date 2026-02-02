
import { VALIDATION_ERROR_TEXTS } from 'lib/Constants'

import { Shape, string, integer } from './types'

const { NUMBER_FORMAT, ZIP_CODE_FORMAT } = VALIDATION_ERROR_TEXTS

const AddressScheme = Shape({
    zip: string()
            .nullable()
            .matches(/^\d{5}$/, NUMBER_FORMAT)
            .length(5, ZIP_CODE_FORMAT)
            .required(),
    city: string().max(256).required(),
    street: string().max(256).required(),
    stateId: integer().required(),
})

export default AddressScheme
