import User from './user/UserInitialState'
import Release from './release/ReleaseInitialState'

const { Record } = require('immutable')

export default Record({
    user: User(),
    release: Release()
})