import { Shape, string } from './types'

const AssessmentVisibilityScheme = Shape({
    comment: string().required()
})

export default AssessmentVisibilityScheme
