import { RESPONSIVE_BREAKPOINTS } from 'lib/Constants'

const { Record } = require('immutable')

const { TABLET_LANDSCAPE } = RESPONSIVE_BREAKPOINTS
const { width } = document.body.getBoundingClientRect()

export default Record({
    items: [],
    isNo: false,
    isOpen: width > TABLET_LANDSCAPE,
    isHidden: true,
})
