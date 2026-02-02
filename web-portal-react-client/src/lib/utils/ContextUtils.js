import config from 'config'

export function path (...paths) {
    const path = paths.join('')

    const isRelevantHost = (
        config.location.host === window?.location?.host
    )

    const root = config.context && isRelevantHost ? `${config.context}` : ''
    return path.charAt(0) === '/' ? `${root}${path}` : `${root}/${path}`
}