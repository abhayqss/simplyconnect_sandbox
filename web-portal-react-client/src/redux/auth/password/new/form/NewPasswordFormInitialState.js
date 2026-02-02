const {Record} = require('immutable')

export default Record({
    error: null,
    isValid: false,
    isFetching: false,
    fields: Record({
        token: '',

        firstName: '',
        firstNameHasError: false,
        firstNameErrorText: '',

        lastName: '',
        lastNameHasError: false,
        lastNameErrorText: '',

        password: '',
        passwordHasError: false,
        passwordErrorText: '',

        confirmPassword: '',
        confirmPasswordHasError: false,
        confirmPasswordErrorText: ''
    })()
})