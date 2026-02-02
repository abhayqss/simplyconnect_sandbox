import Upload from './upload/CanUploadReleaseNoteInitialState'
import Delete from './deletion/CanDeleteReleaseNoteInitialState'

const { Record } = require('immutable');

export default Record({
    upload: Upload(),
    deletion: Delete()
});