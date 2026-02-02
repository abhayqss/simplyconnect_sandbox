import BaseFormValidator from "./BaseFormValidator";

import { interpolate } from 'lib/utils/Utils'
import {
    VALIDATION_ERROR_TEXTS,
    NOTIFICATION_RESPONSIBILITY_TYPES
} from 'lib/Constants'

const {
    EMPTY_FIELD,
    LENGTH_MAXIMUM,
} = VALIDATION_ERROR_TEXTS

const { VIEWABLE, NOT_VIEWABLE } = NOTIFICATION_RESPONSIBILITY_TYPES
const ignoredResponsibilities = [VIEWABLE, NOT_VIEWABLE]

const CONSTRAINTS = {
    employeeOrganizationId: {
        presence: function(_, { employeeOrganizationName }) {
            return !employeeOrganizationName ? {
                allowEmpty: false,
                message: EMPTY_FIELD
            } : false
        }
    },
    employeeOrganizationName: {
        presence: {
            allowEmpty: true,
        }
    },
    employeeId: {
        presence: {
            allowEmpty: false,
            message: EMPTY_FIELD
        }
    },
    roleId: {
        presence: {
            allowEmpty: false,
            message: EMPTY_FIELD
        }
    },
    description: {
        length: {
            maximum: 256,
            message: interpolate(LENGTH_MAXIMUM, 256)
        }
    },
    notificationsPreferences:{
        array: {
            responsibilityName: {
                presence: {
                    allowEmpty: false,
                    message: EMPTY_FIELD
                }
            },
            channels: {
                presence: function(value, notificationPreference) {
                    const { responsibilityName } = notificationPreference

                    if (!value.length && !ignoredResponsibilities.includes(responsibilityName)) {
                        return {
                            allowEmpty: false,
                            message: EMPTY_FIELD
                        }
                    }

                    return false;
                  }
            }
        }
    }
}

class CareTeamFormValidator extends BaseFormValidator {
    validate(data) {
        return super.validate(data, CONSTRAINTS, { fullMessages: false })
    }
}

const validator = new CareTeamFormValidator()
export default validator