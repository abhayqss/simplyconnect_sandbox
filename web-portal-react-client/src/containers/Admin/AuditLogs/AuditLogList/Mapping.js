export const ACTIVITY_TYPES_TO_PATHS = {
    ASSESSMENT_EDIT_GAD_7: { path: '/clients/$0/assessments', target: { name: 'ASSESSMENT', typeName: 'GAD7' } },
    ASSESSMENT_EDIT_PHQ_9: { path: '/clients/$0/assessments', target: { name: 'ASSESSMENT', typeName: 'PHQ9' } },
    ASSESSMENT_EDIT_COMPREHENSIVE: { path: '/clients/$0/assessments', target: { name: 'ASSESSMENT', typeName: 'COMPREHENSIVE' } },

    NOTE_GROUP_EDIT: { path: '/events', target: { name: 'NOTE' } },
    NOTE_EDIT: { path: '/events', target: { name: 'NOTE' } },

    SERVICE_PLAN_UPDATE: { path: '/clients/$0/service-plans', target: { name: 'SERVICE_PLAN' } }
}