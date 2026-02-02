import { useQuery } from 'hooks/common/redux'

import actions from 'redux/report/sdoh/can/mark-as-sent/canMarkAsSentSDoHReportActions'

import { isInteger } from 'lib/utils/Utils'

export default function useCanViewSDoHReportsQuery(params, options) {
    useQuery(actions, params, {
        condition: prev => {
            return isInteger(params.reportId) && (
                prev.reportId !== params.reportId
            )
        },
        ...options
    })
}