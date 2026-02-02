const { Record } = require('immutable')

export default function State(state) {
    return Record({
        error: null,
        fetchCount: 0,
        isFetching: false,
        shouldReload: false,
        ...state,
        setError(e) {
            return this.set('error', e)
        },
        clearError() {
            return this.remove('error')
        },
        setFetching(isFetching = false) {
            return this.set('isFetching', isFetching)
        },
        incFetchCount() {
            return this.set('fetchCount', this.fetchCount + 1)
        },
        clearFetchCount() {
            return this.set('fetchCount', 0)
        },
        setShouldReload(shouldReload = false) {
            return this.set('shouldReload', shouldReload)
        }
    })
}