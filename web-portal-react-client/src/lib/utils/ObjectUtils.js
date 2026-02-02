import {
    all,
    reduce,
    matcher
} from 'underscore'

/*
 Gets nested properties from JavaScript object
 *
 * examples:
 * var prop = getProperty(obj, "prop1.nestedProp1");
 * var prop = getProperty(obj, "prop1.[0].nestedProp1");
 * */

export function getProperty(o, s) {
    if (!o) return
    s = (s + '').replace(/\[(\w+)\]/g, '.$1') // convert indexes to properties
    s = s.replace(/^\./, '') // strip a leading dot

    const a = s.split('.')

    for (let i = 0, n = a.length; i < n; ++i) {
        const k = a[i]

        if (o[k]) o = o[k]
        else return
    }
    return o
}

export function setProperty(target, prop, value) {
    if (typeof prop == 'string') {
        prop = prop.replace(/^\./, '') // strip a leading dot

        if (prop.includes('.')) prop = prop.split('.')
    }

    if (target) {
        if (Array.isArray(prop)) {
            if (prop.length > 1) {
                target[prop[0]] = setProperty(target[prop[0]], prop.slice(1), value)
            }
            else {
                target = setProperty(target, prop[0], value)
            }
        }
        else {
            target[prop] = value
        }
    }
    else {
        target = {}
        if (Array.isArray(prop)) {
            let newValue = value
            for (let idx = prop.length - 1; idx >= 0; idx--) {
                target = {}
                target[prop[idx]] = newValue
                newValue = target
            }
        }
        else {
            target[prop] = value
        }
    }
    return target
}

export function isObject(o) {
    return typeof o === 'object'
}

export function isEmpty(o) {
    if (!o) return true
    if (isObject(o)) return Object.keys(o).length === 0
    if (Array.isArray(o)) return !o?.length
    return false
}

export function keyCount(o) {
    return Object.keys(o).length
}

export function isBlank(o, isStrict = false) {
    return all(o, v => isStrict ? isEmpty(v) : ['', null, undefined].includes(v))
}

export function isEmptyOrBlank(o, isStrict) {
    return isEmpty(o) || isBlank(o, isStrict)
}

export function isNotBlank(o, isStrict = false) {
    return !isBlank(o, isStrict)
}

export function isNotEmptyOrBlank(o, isStrict = false) {
    return !isEmptyOrBlank(o, isStrict)
}

export function has(o, k) {
    return Object.prototype.hasOwnProperty.call(o, k)
}

export function pick(source, predicate) {
    if (!(source && predicate)) return {}

    return Object.keys(source).reduce((res, key) => {
        if (Array.isArray(predicate) && predicate.includes(key)) {
            res[key] = source[key]
        }

        if (typeof predicate === 'function' && predicate(source[key], key, source)) {
            res[key] = source[key]
        }

        return res
    }, {})
}

export function contains(o1, o2) {
    const match = matcher(o2);
    return match(o1);
}

export function keys(o) {
    return Object.keys(o)
}

export function values(o) {
    return Object.values(o)
}

export function getObjectPaths(o, predicate = '') {
    const separator = predicate ? '.' : ''

    if (Array.isArray(o)) return reduce(
        o,
        (acc, _, index) => [...acc, ...getObjectPaths(o[index], `${predicate}${separator}${index}`)],
        []
    )

    if (isObject(o)) return reduce(
        keys(o),
        (acc, key) => [...acc, ...getObjectPaths(o[key], `${predicate}${separator}${key}`)],
        []
    )

    return [predicate]
}
