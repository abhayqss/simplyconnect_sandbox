import { useRef } from 'react'

function useRefCurrent(o = {}) {
    const ref = useRef(o)

    return Object.assign(ref.current, o)
}

export default useRefCurrent
