import NotViewable from './not-viewable/NotViewableEventInitialState'

const { Record } = require('immutable')

const InitialState = Record({
    notViewable: NotViewable()
})

export default InitialState