class ValidationContext {
    constructor({ included, validate, validateAsync }) {
        this.included = included
        this.validate = validate
        this.validateAsync = validateAsync
    }

    setStep(value) {
        this.included.step = value
    }

    executeValidation(strategy) {
        return strategy.execute(this)
    }
}

export default ValidationContext
