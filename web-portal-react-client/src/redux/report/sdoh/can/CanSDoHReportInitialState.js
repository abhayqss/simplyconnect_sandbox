import View from './view/CanViewSDoHReportsInitialState'
import MarkAsSent from './mark-as-sent/CanMarkAsSentSDoHReportInitialState'

const { Record } = require('immutable');

export default Record({
    view: View(),
    markAsSent: MarkAsSent()
});