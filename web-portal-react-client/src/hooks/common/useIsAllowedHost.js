import config from 'config'

export default function useIsAllowedHost() {
	return window?.location?.host.includes(config.location.domain)
}