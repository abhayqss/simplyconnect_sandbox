import {
    useState,
    useEffect
} from 'react'

import { conversations } from 'config'

import { useFetch } from 'hooks/common'

const { provider } = conversations

const HTTP_ACCESS_CONTROL_HEADERS = {
    TWILIO: {
        'Access-Control-Allow-Origin': 'https://viridian-peacock-1566.twil.io'
    }
}

export default function useDownload(params = {}) {
    const [url, setUrl] = useState(params?.url)

    const options = { url }

    if (provider === 'twilio') {
        options.type = params?.contentType
        options.headers = HTTP_ACCESS_CONTROL_HEADERS[provider.toUpperCase()]
    }

    useEffect(() => {
        if (!url && provider === 'twilio' && params?.contentType) {
            params.getContentTemporaryUrl().then(s => {
                setUrl(s)
            })
        }
    }, [url, params, options.type])

    return useFetch(options)
}