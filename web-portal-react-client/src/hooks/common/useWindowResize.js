import { useEffect } from 'react'

import { useDebouncedCallback } from 'use-debounce'

function useWindowResize(callback, { debounceTime = 0 }) {
    const debouncedCallback = useDebouncedCallback(callback, debounceTime)

    useEffect(() => {
        function onResize() {
            debouncedCallback()
        }

        window.addEventListener('resize', onResize)

        return () => {
            window.removeEventListener('resize', onResize)
        }
    }, [debouncedCallback])
}

export default useWindowResize
