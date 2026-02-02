import { useCallback } from 'react'

import service from 'services/ReferralService'

export default function useReferralStatusFormSubmit(data, { requestId }) {
    const {
        status,
        comment,
        referralDeclineReasonId
    } = data

    return useCallback(() => {
        if (status === 'ACCEPTED') {
            return service.acceptRequest(requestId, { comment })
        }

        return service.declineRequest({ comment, referralDeclineReasonId }, requestId)
    }, [
        status,
        comment,
        requestId,
        referralDeclineReasonId
    ])
}