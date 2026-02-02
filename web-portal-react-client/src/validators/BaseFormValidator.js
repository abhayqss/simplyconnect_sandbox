import validate from 'validate.js'
import { each, isEmpty, isFunction, isObject } from 'underscore'

validate.validators.email.PATTERN = /^([a-z0-9!#$%&'*+\\/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+\\/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z]{2,})?$/i

validate.validators.each = function (items, constrains) {
    const errors = []

    each(items, o => {
        const error = validate(o, constrains, { fullMessages: false })
        if (error) errors.push(error)
    })

    return !isEmpty(errors) ? errors : null
}

validate.validators.array = (dataItems, constrains, _, attributes) => {
    const errors = dataItems.reduce((errors, item, index) => {
        const doValidation = isObject(item) ? validate : validate.single

        const error = doValidation(item, constrains, { fullMessages: false, index, context: attributes })

        if (error) {
            errors[index] = error
        }

        return errors
    }, {})

    return isEmpty(errors) ? null : errors
}

validate.validators.optional = (value, options) => {
    return !isEmpty(value) ? validate.single(value, options) : null
}

validate.validators.comparison = (value, options) => {
    const a = isFunction(options.a) ? options.a(value) : options.a
    const b = isFunction(options.b) ? options.b(value) : options.b

    return options.compare(a, b) ? null : options.message 
}

export default class BaseFormValidator {
    validate (data, constraints, options) {
       /* if (excluded) constraints = omit(constraints, excluded)*/

        return validate.async(data, constraints, options)
    }
}