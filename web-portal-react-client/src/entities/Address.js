const { Record } = require('immutable')

const Address = Record({
    city: '',
    street: '',
    stateId: null,
    zip: '',
})

export default Address
