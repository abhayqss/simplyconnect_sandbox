import memoize from 'memoize-one'

export function EntityOrNull(Entity) {
    return memoize(data => data ? new Entity(data) : null)
}