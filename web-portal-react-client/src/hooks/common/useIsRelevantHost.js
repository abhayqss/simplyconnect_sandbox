import config from 'config'

export default function useIsRelevantHost() {
	return config.location.host === window?.location?.host
}