
import { VALIDATION_ERROR_TEXTS } from 'lib/Constants'

import { string } from './types'

const { NUMBER_FORMAT, ZIP_CODE_FORMAT } = VALIDATION_ERROR_TEXTS

const ZipCodeScheme = () => string()
    .nullable()
    .matches(/^[0-9]{5}$/, NUMBER_FORMAT)
    .length(5, ZIP_CODE_FORMAT)

export default ZipCodeScheme