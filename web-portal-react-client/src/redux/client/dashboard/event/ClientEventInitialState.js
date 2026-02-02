import List from './list/ClientEventListInitialState'

const { Record } = require('immutable')

export default Record({
    list: List()
})