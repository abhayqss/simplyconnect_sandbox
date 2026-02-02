import { uniq } from 'underscore'

import { string, Shape, ListOf } from './types'

const Rule = Shape({
    field: string().nullable().required(),
    signature: string().nullable().required()
})

const Scheme = Shape({
    value: ListOf(Rule).test({
        name: 'uniq-rule',
        test: value => uniq(value, o => o.signature + o.field).length === value.length
    })
})

export default Scheme
