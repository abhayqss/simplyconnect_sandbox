import { Store } from 'lib/stores'

const { Record } = require('immutable')

const store = new Store()

export default Record({
    id: store.get('lastViewedClientId') ?? null
})
