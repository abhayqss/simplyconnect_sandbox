import React, {
    useCallback
} from 'react'

import { toast } from 'react-toastify'

import {
    DownloadingStatusInfoAlert
 } from 'components/business/download'

 const DEFAULT_TOAST_OPTIONS = {
    icon: false,
    position: 'bottom-left',
    className: 'DownloadingStatusInfoAlert',
    autoClose: false,
    closeButton: false,
    pauseOnHover: false,
    closeOnClick: false,
    bodyClassName: 'DownloadingStatusInfoAlert-Body',
    hideProgressBar: true,
    pauseOnFocusLoss: false
 }

 const DEFAULT_CONFIG = {
     pending: {
        ...DEFAULT_TOAST_OPTIONS,
        render: () => <DownloadingStatusInfoAlert type="pending" />
     },
     success: {
        ...DEFAULT_TOAST_OPTIONS,
        autoClose: 2000,
        render: () => <DownloadingStatusInfoAlert type="success" />
     },
     error: {
        ...DEFAULT_TOAST_OPTIONS,
        autoClose: 2000,
        render: () => <DownloadingStatusInfoAlert type="error" />
     }
 }

const useDownloadingStatusInfoToast = ({ error, success, pending } = {}) => {
    return useCallback((promise) => toast.promise(
        promise,
        {
            error: { ...DEFAULT_CONFIG.error, ...error },
            success: { ...DEFAULT_CONFIG.success, ...success },
            pending: { ...DEFAULT_CONFIG.pending, ...pending }
        }
    ), [
        error,
        success,
        pending
    ])
}

export default useDownloadingStatusInfoToast