import Can from './can/CanClientDocumentInitialState'
import List from './list/ClientDocumentListInitialState'
import Form from './form/ClientDocumentFormInitialState'
import Count from './count/ClientDocumentCountInitialState'
import Deletion from './deletion/сlientDocumentDeletionInitialState'
import Details from './details/ClientDocumentDetailsInitialState'
import History from './history/ClientDocumentHistoryInitialState'

const { Record } = require('immutable')

const InitialState = Record({
    can: Can(),
    form: Form(),
    list: List(),
    count: Count(),
    details: Details(),
    history:  History(),
    deletion: Deletion()
})

export default InitialState