import { useMemo } from 'react'

import { useDownload } from 'hooks/common'

import service from 'services/AuditLogService'

import { ALLOWED_FILE_FORMATS } from 'lib/Constants'

const { XLSX } = ALLOWED_FILE_FORMATS

const options = {
    doDownload: params => service.download(params),
}

function useAuditLogsDownload(args) {
    const params = useMemo(() => ({
        format: XLSX,
        ...args,
    }), [args])

    return useDownload('AUDIT_LOG', params, options)
}

export default useAuditLogsDownload
