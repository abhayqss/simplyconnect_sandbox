import {
    useState,
    useCallback
} from 'react'

import { isBoolean } from 'underscore'

export default function useToggle(initialState = false) {
    const [state, setState] = useState(initialState)

    const toggle = useCallback(
        (s = undefined) => setState(state => isBoolean(s) ? s : !state), []
    )

    return [state, toggle]
}