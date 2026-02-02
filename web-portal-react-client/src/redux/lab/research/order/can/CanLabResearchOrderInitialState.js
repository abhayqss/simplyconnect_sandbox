import Add from './add/CanAddLabResearchOrderInitialState'
import Review from './review/CanReviewLabResearchOrderInitialState'

const { Record } = require('immutable');

export default Record({
    add: Add(),
    review: Review()
});