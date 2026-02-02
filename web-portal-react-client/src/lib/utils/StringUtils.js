export function concatIf(s1, s2, condition) {
    if (typeof s2 === 'string') {
        return s1 + (condition ? s2 : '')
    }

    if (typeof s2 === 'object') {
        let s = s1

        for (const k in s2) {
            s += s2[k] ? k : ''
        }

        return s
    }

    return s1
}

export function isString(v) {
    return typeof v === 'string'
}

export function isEmpty(v) {
    if ([null, undefined].includes(v)) return true
    return isString(v) && v.length === 0
}

export function eqIgnoreCase(str1, str2) {
    return str1.toLowerCase() === str2.toLowerCase()
}

export function addAsterix(title) {
    return {
        if(condition) {
            return `${title}${condition ? '*' : ''}`
        },
        end() {
            return `${title}*`
        }
    }
}