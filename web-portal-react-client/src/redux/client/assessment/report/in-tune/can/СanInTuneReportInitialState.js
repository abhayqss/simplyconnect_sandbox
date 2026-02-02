import Download from './download/CanDownloadInTuneReportInitialState'
import Generate from './generate/CanGenerateInTuneReportInitialState'

const { Record } = require('immutable')

export default Record({
    download: Download(),
    generate: Generate()
})