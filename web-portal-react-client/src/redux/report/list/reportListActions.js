import { Actions } from 'redux/utils/List'

import validator from 'validators/ReportFilterValidator'

import actionTypes from './reportListActionTypes'

const { VALIDATE_FILTER } = actionTypes

export default {
    ...Actions({
        actionTypes,
        isPageable: false,
        isSortable: false
    }),
    validate(data, options) {
        return dispatch => validator.validate(data, options).then(success => {
            dispatch({ type: VALIDATE_FILTER, payload: { success } })
            return true
        }).catch(errors => {
            dispatch({ type: VALIDATE_FILTER, payload: { success: false, errors } })
            return false
        })
    }
}