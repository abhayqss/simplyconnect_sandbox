import { map, each, isArray } from 'underscore'

/*
* mapping = [{ sourceProp: X, destProp: Y }]
* */

export default function useMapped(data, mapping = []) {
    return isArray(data) ? map(data, o => {
        const mapped = {}

        each(mapping, ({ sourceProp, destProp }) => {
            mapped[destProp] = o[sourceProp]
        })

        return { ...o, ...mapped }
    }) : data
}