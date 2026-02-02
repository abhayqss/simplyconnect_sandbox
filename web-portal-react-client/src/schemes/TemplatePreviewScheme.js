import { Shape, ListOf } from './types'

const SignatureRequestScheme = Shape({
    signatures: ListOf()
        .test(
            'validate signatures',
            'A placeholder cannot overlap another placeholder. Please move it to another place.',
            (rects) => {
                return !rects.length || rects.some(rect => {
                    const rest = rects.filter(o => o !== rect)

                    return !rest.length || rest.every(o => {
                        const a = rect
                        const b = o

                        return (
                            a.topLeftX >= b.bottomRightX
                            || b.topLeftX >= a.bottomRightX
                            || a.topLeftY >= b.bottomRightY
                            || b.topLeftY >= a.bottomRightY
                        )
                    })
                })
            }
        )
})

export default SignatureRequestScheme
