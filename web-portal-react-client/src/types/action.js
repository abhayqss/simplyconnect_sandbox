import PropTypes from 'prop-types'

const MOUNTING_PHASE = 'mounting'
const UNMOUNTING_PHASE = 'unmounting'

export default PropTypes.shape({
    action: PropTypes.func,
    params: PropTypes.object,
    onPerformed: PropTypes.func,
    shouldPerform: PropTypes.func,
    isMultiple: PropTypes.bool,
    performingPhase: PropTypes.oneOf([MOUNTING_PHASE, UNMOUNTING_PHASE])
})
