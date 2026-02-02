import { number } from 'yup'

import { uc, isEmpty } from 'lib/utils/Utils'

import { Shape, string } from './types'

const ONE_MB = 1024 * 1024

const FileScheme = ({ maxMB, format, allowedTypes = [], allowedFormats = [] }) => Shape({
    name: string().required(),
    size: number().test({
        name: 'maxSize',
        message: `The maximum file size for uploads is ${maxMB} MB`,
        test(value) {
            return value <= maxMB
        }
    }),
    type: string().test({
        name: 'allowedTypes',
        message: 'The selected format is not allowed',
        test(value) {
            return (
                isEmpty(allowedTypes) || allowedTypes.includes(value)
            ) && (
                isEmpty(allowedFormats) || allowedFormats.includes(uc(format))
            )
        }
    }),
}).transform((_, value) => ({
    name: value?.name,
    size: value ? value.size / ONE_MB : 0,
    type: value?.type,
}))

export const FileSchemeNullable = (
    { maxMB, format, allowedTypes, allowedFormats }
) => FileScheme(
    { maxMB, format, allowedTypes, allowedFormats }
).transform((_, value) => value ? ({
    name: value?.name,
    size: value ? value.size / ONE_MB : 0,
    type: value?.type
}) : null).nullable()

export default FileScheme
