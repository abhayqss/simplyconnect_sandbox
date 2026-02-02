import { range } from 'underscore'

import BaseValidationStrategy from './BaseValidationStrategy'
import SimpleValidationStrategy  from './SimpleValidationStrategy'

class ComplexValidationStrategy extends BaseValidationStrategy {
    execute(context) {
        const { step } = context

        let promises = range(step + 1).map(s => {
            let strategy = new SimpleValidationStrategy()

            return strategy.execute({ ...context, step: s })
        })

        return Promise.all(promises).then(results => {
            let result = results.find(o => !o.isValid) || { isValid: true }

            return result
        })
    }
}

export default ComplexValidationStrategy
