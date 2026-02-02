import { useState, useEffect } from 'react'

export default function(media) {
    const [url, setUrl] = useState(null)

    useEffect(() => {
        if (media) {
            media.getUrl().then(setUrl)
        }
    }, [media])

    return url
}