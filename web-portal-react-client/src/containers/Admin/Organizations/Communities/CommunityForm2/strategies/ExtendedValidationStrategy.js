import { range } from 'underscore'

import BaseValidationStrategy from './BaseValidationStrategy'
import RegularValidationStrategy  from './RegularValidationStrategy'

class ExtendedValidationStrategy extends BaseValidationStrategy {
    execute(context) {
        const { included } = context
        const { step } = included

        let promises = range(step + 1).map(s => {
            let strategy = new RegularValidationStrategy()

            return strategy.execute({ ...context, step: s })
        })

        return Promise.all(promises).then(results => {
            let result = results.find(o => !o.isValid) || { isValid: true }

            return result
        })
    }
}

export default ExtendedValidationStrategy
