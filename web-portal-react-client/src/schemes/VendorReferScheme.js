import {array, bool, number, object} from 'yup'



import validate from 'validate.js'

import { Shape, string, integer, phoneNumber, ListOf } from './types'

import {
    ALLOWED_FILE_FORMATS,
    VALIDATION_ERROR_TEXTS,
    ALLOWED_FILE_FORMAT_MIME_TYPES,
} from 'lib/Constants'

import { map } from 'lib/utils/ArrayUtils'

import Address from './AddressScheme'
import FileScheme, { FileSchemeNullable } from './FileScheme'
import { getFileExtension } from '../lib/utils/FileUtils'
import { isNotBlank, isBlank, isEmpty } from '../lib/utils/ObjectUtils'
import ZipCodeScheme from "./ZipCodeScheme";
import {any} from "underscore";

const {
    EMAIL_FORMAT,
} = VALIDATION_ERROR_TEXTS

const { PATTERN: EMAIL_PATTERN } = validate.validators.email


const VendorScheme = Shape(
    {
        sourceCommunityId: string().nullable().required(),
        sourceOrganizationId: string().nullable().required(),
        sourceContactId:string().nullable().required(),
        sourceAddress: string().nullable().required(),
        sourcePhone: phoneNumber().nullable().required(),
        contactWay: string().nullable().required(),
        /*sourceContactEmail: string().nullable().required().matches(EMAIL_PATTERN, {
            message: EMAIL_FORMAT,
            excludeEmptyString: true,
        }),*/
        sourceCommunityEmail: string().nullable().required().matches(EMAIL_PATTERN, {
            message: EMAIL_FORMAT,
            excludeEmptyString: true,
        }),
        referContent: string().nullable().required().max(520),
        referObject: string().nullable().required(),
    }, [['medicaidNumber', 'medicareNumber']])

export default VendorScheme
