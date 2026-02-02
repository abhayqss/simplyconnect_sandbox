import { VALIDATION_ERROR_TEXTS } from 'lib/Constants'

import { ListOf, Shape, string } from './types'

const { EMPTY_FIELD } = VALIDATION_ERROR_TEXTS

const TemplateBuilderPreviewScheme = Shape({
    type: string().required(),
    name: string().max(36).required(),
    communityIds: ListOf().nullable()
        .when(
            ['$included', 'type'],
            (included, type, scheme) => (
                type === "COMMUNITY"
                    ? scheme.min(1, EMPTY_FIELD)
                    : scheme.optional()
            )
        )
})

export default TemplateBuilderPreviewScheme