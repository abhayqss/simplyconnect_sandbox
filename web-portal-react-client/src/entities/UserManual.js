const { Record } = require('immutable')

const UserManual = Record({
    title: '',
    file: Record({
        name: '',
        size: null,
        type: '',
    })()
})

export default UserManual
