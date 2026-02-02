import Can from './can/CanReportInitialState'
import List from './list/ReportListInitialState'
import SDoH from './sdoh/SDoHReportInitialState'
import Document from './document/ReportDocumentInitialState'

const { Record } = require('immutable')

export default Record({
    can: Can(),
    list: List(),
    sdoh: SDoH(),
    document: Document()
})