import {
    bool,
    func,
    string
} from 'prop-types'

import TList from './List'

export default {
    ...TList,
    isDownloading: bool,
    hasTypeCol: bool,
    hasSizeCol: bool,
    hasStatusCol: bool,
    hasAuthorCol: bool,
    hasActionsCol: bool,
    hasCreatedDateCol: bool,
    hasDescriptionCol: bool,
    hasLastModifiedDateCol: bool,
    hasSignatureStatusCol: bool,
    onSort: func,
    onView: func,
    onDownloadSingle: func,
    onDownloadMultiple: func,
    onDownloadAll: func,
    onDelete: func,
    onRestore: func,
    onRefresh: func,
    className: string,
    captionClass: string
}