import { useMutation } from '@tanstack/react-query'

import service from 'services/CommunityService'

export default function useServerSignedCertificateMutation(options) {
    return useMutation(params => service.loadServerSelfSignedCertificate(params), options)
}
