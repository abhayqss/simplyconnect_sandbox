import UrlPattern from 'url-pattern'
import queryString from 'query-string'

import authUserStore from 'lib/stores/AuthUserStore'

export function pattern(t) {
    return new UrlPattern(t)
}

export function matches(t, p) {
    return pattern(t).match(p)
}

export function getUrl({ resources = [], params = {} }) {
    let url = '/'

    for (let v of resources) {
        const sp = url.endsWith('/') ? '' : '/'

        if (typeof v === 'string') {
            url += sp + v; continue
        }

        const hasId = ![null, undefined].includes(v.id)

        if (hasId) url += `${sp}${v.name}/${v.id}`
        else if (v.hasId === false) url += `${sp}${v.name}`
    }

    return url
}

export function getQueryParamsString(params) {
    return queryString.stringify(params)
}

export function getQueryParams(search, options, keys) {
    const params = queryString.parse(search, {
        parseNumbers: true,
        parseBooleans: true,
        arrayFormat: 'comma',
        ...options
    })

    if (keys?.length) return keys.reduce((obj, key) => {
        if (params) obj[key] = params[key]
        return obj
    }, {})

    return params
}

export function getAllowedRoutes(routes) {
    const role = authUserStore.get()?.roleName

    return routes.filter(({ permission }) => {
        return !permission || permission.includes(role)
    })
}

export function getAllAllowedRoutes(routes, parentPath = '') {
    function pathMapper(route) {
        return {
            ...route,
            path: parentPath + route.path
        }
    }

    const allowedRoutes = getAllowedRoutes(routes.map(pathMapper))

    return allowedRoutes.reduce((routes, route) => {
        if (route.children) {
            routes = [...routes, ...getAllAllowedRoutes(route.children, route.path)]
        }

        return routes
    }, [...allowedRoutes])
}

export function getAbsoluteUrl(url, protocol = 'https') {
    return matches(/^(http(s)?:\/\/.)/, url) ? url : `${protocol}://${url}`
}