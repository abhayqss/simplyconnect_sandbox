import { useCallback } from 'react'

import service from 'services/AssessmentService'

import { ASSESSMENT_STATUSES } from 'lib/Constants'

const { HIDDEN } = ASSESSMENT_STATUSES

export default function useAssessmentVisibilityFormSubmit(
    {
        comment
    },
    {
        clientId,
        assessmentId,
        assessmentStatus
    }
) {
    return useCallback(() => {
        return assessmentStatus === HIDDEN ? (
            service.restoreById(assessmentId, { comment, clientId })
        ) : (
            service.hideById(assessmentId, { comment, clientId })
        )
    }, [
        comment,
        clientId,
        assessmentId,
        assessmentStatus
    ])
}