const { Record } = require('immutable')

const ReleaseNote = Record({
    file: Record({
        name: '',
        size: null,
        type: '',
    })(),
    description: '',

    isEmailNotificationEnabled: false,
    isInAppNotificationEnabled: false,

    features: '',
    fixes: ''
})

export default ReleaseNote
