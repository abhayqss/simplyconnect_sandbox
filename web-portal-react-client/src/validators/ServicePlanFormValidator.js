import BaseFormValidator from "./BaseFormValidator";

import { getPasswordRegExp, interpolate } from 'lib/utils/Utils'

import {
    VALIDATION_ERROR_TEXTS,
    SERVICE_PLAN_NEED_DOMAINS
} from 'lib/Constants'

import { each } from 'underscore'

const {
    EMPTY_FIELD,
    EMAIL_FORMAT,
    PHONE_FORMAT,
    LENGTH_MAXIMUM,
} = VALIDATION_ERROR_TEXTS

const {
    EDUCATION_TASK
} = SERVICE_PLAN_NEED_DOMAINS

const CONSTRAINTS = {
    dateCreated: {
        presence: {
            allowEmpty: false,
            message: EMPTY_FIELD
        }
    },
    createdBy: {
        presence: {
            allowEmpty: false,
            message: EMPTY_FIELD
        }
    },
    'clinicianReview.wasReviewedWithMember': (value, attributes, attributeName, options) => {
        const constraints = {
            presence: {
                allowEmpty: false,
                message: EMPTY_FIELD
            }
        }

        return options.canReviewByClinician ? constraints : null
    },
    'clinicianReview.dateOfReviewWithMember': (value, attributes, attributeName, options) => {
        const constraints = {
            presence: {
                allowEmpty: false,
                message: EMPTY_FIELD
            }
        }

        return options.canReviewByClinician && attributes.clinicianReview.wasReviewedWithMember ? constraints : null
    },
    'clinicianReview.wasCopyReceivedByMember': (value, attributes, attributeName, options) => {
        const constraints = {
            presence: {
                allowEmpty: false,
                message: EMPTY_FIELD
            }
        }

        return options.canReviewByClinician ? constraints : null
    },
    'clinicianReview.dateOfCopyWasReceivedByMember': (value, attributes, attributeName, options) => {
        const constraints = {
            presence: {
                allowEmpty: false,
                message: EMPTY_FIELD
            }
        }

        return options.canReviewByClinician && attributes.clinicianReview.wasCopyReceivedByMember ? constraints : null
    },
    'clinicianReview.copyWasNotReceivedNotes': (value, attributes, attributeName, options) => {
        const constraints = {
            presence: {
                allowEmpty: false,
                message: EMPTY_FIELD
            }
        }

        return options.canReviewByClinician && attributes.clinicianReview.wasCopyReceivedByMember === false ? constraints : null
    },
    'clinicianReview.isClientLSSProgramParticipant': (value, attributes, attributeName, options) => {
        const constraints = {
            presence: {
                allowEmpty: false,
                message: EMPTY_FIELD
            }
        }

        return options.canReviewByClinician ? constraints : null
    },
    'clinicianReview.lssPrograms': (value, attributes, attributeName, options) => {
        const constraints = {
            presence: {
                allowEmpty: false,
                message: EMPTY_FIELD
            }
        }

        return options.canReviewByClinician && attributes.clinicianReview.isClientLSSProgramParticipant ? constraints : null
    }
}

class ServicePlanFormValidator extends BaseFormValidator {
    validate(data, options) {
        let constraints = CONSTRAINTS

        each(data.needs, (need, i) => {
            constraints = {
                ...constraints,
                [`needs.${i}.fields.domainId`]: {
                    presence: {
                        allowEmpty: false,
                        message: EMPTY_FIELD
                    }
                },
                [`needs.${i}.fields.priorityId`]: {
                    presence: {
                        allowEmpty: false,
                        message: EMPTY_FIELD
                    }
                },
                ...need.fields.domainName === EDUCATION_TASK ? {
                    [`needs.${i}.fields.activationOrEducationTask`]: {
                        presence: {
                            allowEmpty: false,
                            message: EMPTY_FIELD
                        },
                        length: {
                            maximum: 20000,
                            tooLong: interpolate(LENGTH_MAXIMUM, 20000)
                        }
                    },
                    [`needs.${i}.fields.targetCompletionDate`]: {
                        presence: {
                            allowEmpty: false,
                            message: EMPTY_FIELD
                        }
                    }
                } : {
                    [`needs.${i}.fields.needOpportunity`]: {
                        presence: {
                            allowEmpty: false,
                            message: EMPTY_FIELD
                        },
                        length: {
                            maximum: 20000,
                            tooLong: interpolate(LENGTH_MAXIMUM, 20000)
                        }
                    },
                    [`needs.${i}.fields.proficiencyGraduationCriteria`]: {
                        length: {
                            maximum: 5000,
                            tooLong: interpolate(LENGTH_MAXIMUM, 5000)
                        }
                    }
                }
            }

            each(need.fields.goals, (goal, j) => {
                constraints = {
                    ...constraints,
                    [`needs.${i}.fields.goals.${j}.fields.goal`]: {
                        presence: {
                            allowEmpty: false,
                            message: EMPTY_FIELD
                        },
                        length: {
                            maximum: 256,
                            tooLong: interpolate(LENGTH_MAXIMUM, 256)
                        }
                    },
                    [`needs.${i}.fields.goals.${j}.fields.barriers`]: {
                        length: {
                            maximum: 5000,
                            tooLong: interpolate(LENGTH_MAXIMUM, 5000)
                        }
                    },
                    [`needs.${i}.fields.goals.${j}.fields.interventionAction`]: {
                        length: {
                            maximum: 5000,
                            tooLong: interpolate(LENGTH_MAXIMUM, 5000)
                        }
                    },
                    [`needs.${i}.fields.goals.${j}.fields.providerName`]: {
                        length: {
                            maximum: 256,
                            tooLong: interpolate(LENGTH_MAXIMUM, 256)
                        }
                    },
                    [`needs.${i}.fields.goals.${j}.fields.providerEmail`]: {
                        email: {
                            message: EMAIL_FORMAT
                        }
                    },
                    [`needs.${i}.fields.goals.${j}.fields.providerPhone`]: {
                        format: {
                            pattern: /(^$)|^(\+?\d{10,16})$/,
                            message: PHONE_FORMAT
                        }
                    },
                    [`needs.${i}.fields.goals.${j}.fields.resourceName`]: {
                        length: {
                            maximum: 256,
                            tooLong: interpolate(LENGTH_MAXIMUM, 256)
                        }
                    },
                    [`needs.${i}.fields.goals.${j}.fields.isOngoingService`]: {
                        presence: {
                            allowEmpty: false,
                            message: EMPTY_FIELD
                        }
                    },
                    [`needs.${i}.fields.goals.${j}.fields.contactName`]: {
                        length: {
                            maximum: 256,
                            tooLong: interpolate(LENGTH_MAXIMUM, 256)
                        }
                    },
                    [`needs.${i}.fields.goals.${j}.fields.targetCompletionDate`]: {
                        presence: {
                            allowEmpty: false,
                            message: EMPTY_FIELD
                        }
                    }
                }
            })
        })

        return super.validate(data, constraints, { fullMessages: false, ...options })
    }
}

export default new ServicePlanFormValidator()