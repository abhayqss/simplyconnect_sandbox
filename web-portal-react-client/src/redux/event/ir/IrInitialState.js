import Can from './can/CanIrInitialState'
import Details from './details/IrDetailsInitialState'

const { Record } = require('immutable');

export default Record({
    can: Can(),
    details: Details()
})