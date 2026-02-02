class ValidationContext {
    constructor({ step, validate, validateAsync }) {
        this.step = step
        this.validate = validate
        this.validateAsync = validateAsync
    }

    setStep(value) {
        this.step = value
    }

    executeValidation(strategy) {
        return strategy.execute(this)
    }
}

export default ValidationContext
