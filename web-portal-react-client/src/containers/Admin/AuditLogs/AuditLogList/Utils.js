import { camelCase } from 'camel-case'

import {
    interpolate
} from 'lib/utils/Utils'

import {
    path
} from 'lib/utils/ContextUtils'

import { ACTIVITY_TYPES_TO_PATHS } from './Mapping'

export function getActivityLocation(activityType, params = {}) {
    const { clientId, relatedId } = params
    let { path: pathname, target } = ACTIVITY_TYPES_TO_PATHS[activityType] || {}

    if (pathname) {
        if (clientId && !pathname.includes('/clients')) {
            pathname = `/clients/$0${pathname}`
        }

        if (clientId) pathname = interpolate(pathname, clientId)

        pathname = path(pathname)

        const state = { [camelCase(target.name) + 'Id']: relatedId }
        if (target.typeName) state[camelCase(target.name + 'TypeName')] = target.typeName

        return { pathname, state }
    }

    return null
}
