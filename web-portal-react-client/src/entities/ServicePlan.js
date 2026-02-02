const { Record, List } = require('immutable')

export default Record({
    id: null,

    /* Summary */
    dateCreated: null,
    dateCreatedHasError: false,
    dateCreatedErrorText: '',

    dateCompleted: null,
    dateCompletedHasError: false,
    dateCompletedErrorText: '',

    createdBy: '',
    createdByHasError: false,
    createdByErrorText: '',

    isCompleted: false,
    isCompletedHasError: false,
    isCompletedErrorText: '',
    
    clientHasAdvancedDirectiveOnFile: false,

    /* Clinician Review (Nar Cal CR) */
    clinicianReview: Record({
        wasReviewed: false,

        reviewNotes: null,

        wasReviewedWithMember: null,
        wasReviewedWithMemberHasError: false,
        wasReviewedWithMemberErrorText: '',

        dateOfReviewWithMember: null,
        dateOfReviewWithMemberHasError: false,
        dateOfReviewWithMemberErrorText: '',

        wasCopyReceivedByMember: null,
        wasCopyReceivedByMemberHasError: false,
        wasCopyReceivedByMemberErrorText: '',

        dateOfCopyWasReceivedByMember: null,
        dateOfCopyWasReceivedByMemberHasError: false,
        dateOfCopyWasReceivedByMemberErrorText: '',

        copyWasNotReceivedNotes: null,
        copyWasNotReceivedNotesHasError: false,
        copyWasNotReceivedNotesErrorText: '',

        isClientLSSProgramParticipant: null,
        isClientLSSProgramParticipantHasError: false,
        isClientLSSProgramParticipantErrorText: '',

        lssPrograms: null,
        lssProgramsHasError: null,
        lssProgramsErrorText: null,
    })(),

    /* Need / Opportunities */
    needs: List([]),

    scoring: []
})