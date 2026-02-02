import actions from 'redux/client/document/can/add/canAddClientDocumentActions'

import useQuery from './useQuery'

export default function useCanAddDocumentQuery(params, options) {
    return useQuery(actions, params, options)
}