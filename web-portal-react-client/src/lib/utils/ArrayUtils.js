export function isArray(array) {
    return Array.isArray(array)
}

export function first(array, n) {
    if (Array.isArray(array)) {
        if (n > 0) {
            const res = []
            for (let i = 0; i < n; i++) {
                res.push(array[i])
            }
            return res
        }
        return array[0]
    }
}

export function last(array, n) {
    if (Array.isArray(array)) {
        if (n > 0) {
            const res = []
            for (let i = array.length - 1; i > array.length - 1 - n; i--) {
                res.push(array[i])
            }
            return res
        }
        return array[array.length - 1]
    }
}

export function isEmpty(array) {
    return !array?.length
}

export function isNotEmpty(array) {
    return array?.length > 0
}

export function isUnary(array) {
    return array?.length === 1
}

export function isBinary(array) {
    return array?.length === 2
}

export function unshiftIf(array, o, condition = true) {
    if (typeof condition === 'boolean' && condition) {
        return [o, ...array]
    }

    if (typeof condition === 'function' && condition()) {
        return [o, ...array]
    }

    return array
}

export function pushIf(array, o, condition = true) {
    if (typeof condition === 'boolean' && condition) {
        return [...array, o]
    }

    if (typeof condition === 'function' && condition()) {
        return [...array, o]
    }

    return array
}

export function builder(src = []) {
    let arr = [...src]

    return {
        unshift: function (o) {
            arr.unshift(o)
            return this
        },
        unshiftIf: function (o, condition) {
            unshiftIf(arr, o, condition)
            return this
        },
        push: function (o) {
            arr.push(o)
            return this
        },
        pushIf: function (o, condition) {
            arr = pushIf(arr, o, condition)
            return this
        },
        end: function () {
            return arr
        }
    }
}

export function compact(array) {
    return Array.isArray(array) ? array.filter(Boolean) : array
}

export function map(array, mapper) {
    return Array.isArray(array) ? array.map(mapper) : []
}

export function part(source, start, count) {
    if (!source) return []
    let sign = count >= 0 ? 1 : -1
    return source.slice(start, sign * count)
}

export function reject(arr, predicate) {
    const invert = function (f) {
        return function (x, i) {
            return !f(x, i)
        }
    }
    return arr.filter(invert(predicate))
}

export function count(arr = [], predicate) {
    if (!arr) return 0
    if (!predicate) return arr.length

    let count = 0

    for (let i = 0; i < arr.length; i++) {
        if (predicate(arr[i], i)) count++
    }

    return count
}

export function isEqLength(arr1, arr2) {
    return arr1?.length === arr2?.length
}

export function isNotEqLength(arr1, arr2) {
    return !isEqLength(arr1, arr2)
}

export function moveItem(arr, predicate, pozIndex) {
    if (!arr) return arr
    if (!predicate) return arr

    const res = [...arr]
    const index = arr.findIndex(predicate)

    if (index >= 0) {
        res.splice(index, 1)
        res.splice(pozIndex, 0, arr[index])
    }

    return res
}