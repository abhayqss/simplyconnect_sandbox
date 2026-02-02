import Upload from './upload/CanUploadUserManualInitialState'
import Delete from './deletion/CanDeleteUserManualInitialState'

const { Record } = require('immutable');

export default Record({
    upload: Upload(),
    deletion: Delete()
});