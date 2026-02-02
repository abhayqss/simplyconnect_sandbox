import { Actions } from 'redux/utils/Delete'

import service from 'services/ClientDocumentService'

import actionTypes from './сlientDocumentDeletionActionTypes'

export default Actions({
    actionTypes,
    doDelete: ({ documentId, ...params }) => service.deleteById(documentId, params)
})