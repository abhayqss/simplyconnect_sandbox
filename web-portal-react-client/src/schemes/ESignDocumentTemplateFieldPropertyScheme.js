import { string, Shape } from './types'

const specialSymbolsRegex = /\\|\/|:|\*|\?|"|<|>|\|/
const errorMessage = `Special symbols are not allowed`

const Scheme = Shape({
    label: string().max(30).required()
        .test('no-special-symbols', errorMessage, value => !specialSymbolsRegex.test(value)),
    value: string().max(256).when(
        ['$included'], (included, scheme) => (
            included.hasValue ? scheme.required() : scheme.optional()
        )
    ),
})

export default Scheme
