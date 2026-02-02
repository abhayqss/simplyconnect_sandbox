import { useLocation } from 'react-router-dom'

export default function useExternalProviderUrlCheck() {
    const location = useLocation()
    return location.pathname.includes('external-provider')
}