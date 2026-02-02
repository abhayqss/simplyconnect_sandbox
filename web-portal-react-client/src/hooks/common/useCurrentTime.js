import { useEffect, useState } from 'react'

export default function useCurrentTime(interval) {
    const [currentTime, setCurrentTime] = useState(Date.now())

    useEffect(() => {
        const id = setInterval(() => {
            setCurrentTime(Date.now())
        }, interval)

        return () => clearInterval(id)
    }, [interval])

    return currentTime
}