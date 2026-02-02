const { Record, List } = require('immutable')

export default Record({
    id: null,

    domainId: null,
    domainIdHasError: false,
    domainIdErrorText: '',

    domainName: null,

    programTypeId: null,
    programTypeIdHasError: false,
    programTypeIdErrorText: '',

    programTypeName: null,
    programTypeTitle: null,

    programSubTypeId: null,
    programSubTypeIdHasError: false,
    programSubTypeIdErrorText: '',

    programSubTypeName: null,
    programSubTypeTitle: null,

    priorityId: null,
    priorityIdHasError: false,
    priorityIdErrorText: '',

    activationOrEducationTask: '',
    activationOrEducationTaskHasError: false,
    activationOrEducationTaskErrorText: '',

    completionDate: '',
    completionDateHasError: false,
    completionDateErrorText: '',

    needOpportunity: '',
    needOpportunityHasError: false,
    needOpportunityErrorText: '',

    targetCompletionDate: '',
    targetCompletionDateHasError: false,
    targetCompletionDateErrorText: '',

    proficiencyGraduationCriteria: '',
    proficiencyGraduationCriteriaHasError: false,
    proficiencyGraduationCriteriaErrorText: '',

    goals: List([])
})