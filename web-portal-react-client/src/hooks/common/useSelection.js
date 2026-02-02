import {
    useRef,
    useMemo,
    useState,
    useCallback
} from 'react'

const _getKey = o => o.id

const arrayToMap = (arr, mapper) => new Map(
    arr?.map(o => [mapper(o), o])
)

export default function useSelection(
    items = [],
    defaultSelected = [],
    options = {}
) {
    const optionsRef = useRef({})
    optionsRef.current = options

    const getKey = useCallback(item => (
        optionsRef.current.getKey ? optionsRef.current.getKey(item) : _getKey(item)
    ), [optionsRef])

    const [selected, setSelected] = useState(defaultSelected)

    const selectedMap = useMemo(
        () => arrayToMap(selected, getKey),
        [getKey, selected]
    )

    const areAllSelected = useMemo(
        () => items?.every(item => selectedMap.has(getKey(item))),
        [items, getKey, selectedMap]
    )

    const has = useCallback(
        (item) => selectedMap.has(getKey(item)),
        [getKey, selectedMap]
    )

    const clear = useCallback(() => setSelected([]), [])

    const select = useCallback((item) => {
        const map = new Map(selectedMap)
        map.set(getKey(item), item)
        return setSelected(Array.from(map.values()))
    }, [getKey, selectedMap])

    const unSelect = useCallback((item) => {
        const map = new Map(selectedMap)
        map.delete(getKey(item))
        return setSelected(Array.from(map.values()))
    }, [getKey, selectedMap])

    const toggle = useCallback((item) => {
        if (has(item)) unSelect(item)
        else select(item)
    }, [
        has,
        select,
        unSelect
    ])

    const selectAll = useCallback(() => {
        const map = new Map()

        items?.forEach(o => {
            map.set(getKey(o), o)
        })

        return setSelected(Array.from(map.values()))
    }, [items, getKey])

    const unSelectAll = useCallback(() => {
        const map = new Map(selectedMap)

        items?.forEach(o => {
            map.delete(getKey(o))
        })

        return setSelected(Array.from(map.values()))
    }, [
        items,
        getKey,
        selectedMap
    ])

    return {
        selected,
        areAllSelected,
        
        has,
        clear,
        select,
        toggle,
        unSelect,
        selectAll,
        unSelectAll,
    }
}