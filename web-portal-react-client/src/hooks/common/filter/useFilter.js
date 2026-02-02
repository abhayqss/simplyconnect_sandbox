import { useEffect, useCallback } from 'react'

import { noop } from 'underscore'

import { useStore, useRefCurrent } from 'hooks/common/index'

export default function useFilter(
    name,
    data,
    {
        onClear = noop,
        onApply = noop,
        onRestore = noop
    } = {}
) {
    const params = useRefCurrent({ data })

    const options = useRefCurrent(
        { onClear, onApply, onRestore }
    )

    const store = useStore()

    const isSaved = useCallback(() => {
        return !!store.get(name)
    }, [name, store])

    const apply = useCallback(() => {
        options.onApply(params.data)
    }, [params, options])

    const remove = useCallback(() => {
        store.clear(name)
    }, [name, store])

    const save = useCallback((o = {}, shouldMerge = true) => {
        store.save(name, { ...shouldMerge && params.data, ...o })
    }, [name, store, params])

    const restore = useCallback(() => {
        options.onRestore(store.get(name) || {})
    }, [name, store, options])

    useEffect(() => {
        restore()
    }, [restore])

    return {
        save,
        apply,
        remove,
        restore,
        isSaved
    }
}