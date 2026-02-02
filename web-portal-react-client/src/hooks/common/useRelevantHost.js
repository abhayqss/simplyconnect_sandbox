import config from 'config'

export default function useRelevantHost() {
	return config.location.host
}