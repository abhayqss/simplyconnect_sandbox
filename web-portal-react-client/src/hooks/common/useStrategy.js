import { useCallback } from 'react'

function useStrategy(Strategy) {
    const execute = useCallback((context) => {
        return Strategy(context)
    }, [Strategy])

    return { execute }
}

export default useStrategy