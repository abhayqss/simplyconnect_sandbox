import { Actions } from 'redux/utils/Details'

import service from 'services/ClientDocumentService'

import actionTypes from './clientDocumentDetailsActionTypes'

export default Actions({
    actionTypes,
    doLoad: ({ documentId, ...params }) => service.downloadHtmlById(documentId, params),
    doDownload: ({ documentId, ...params }) => service.downloadById(documentId, params)
})