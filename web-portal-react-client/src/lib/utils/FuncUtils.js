export function noop() { }

export function isFunction(f) {
    return typeof f === 'function'
}

export function partial(fn, ...boundArgs) {
    return function (...args) {
        return fn(...boundArgs, ...args)
    }
}