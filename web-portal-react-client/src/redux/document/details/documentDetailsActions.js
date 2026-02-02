import { Actions } from 'redux/utils/Details'

import service from 'services/DocumentService'

import actionTypes from './documentDetailsActionTypes'

export default Actions({
    actionTypes,
    doDownload: ({ documentId, ...params }) => service.downloadById(documentId, params)
})