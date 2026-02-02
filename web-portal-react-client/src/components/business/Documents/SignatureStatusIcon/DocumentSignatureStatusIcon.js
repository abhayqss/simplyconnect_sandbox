import React, {
    memo,
    useRef,
    useState,
    useEffect
} from 'react'

import { UncontrolledTooltip as Tooltip } from 'reactstrap'

import { E_SIGN_STATUSES } from 'lib/Constants'

import { ReactComponent as FileFailed } from 'images/file-failed.svg'
import { ReactComponent as FileRequested } from 'images/file-send.svg'
import { ReactComponent as FileSuccess } from 'images/file-success.svg'
import { ReactComponent as FileExpired } from 'images/file-expired.svg'
import { ReactComponent as FileCancelled } from 'images/file-cancelled.svg'

const {
    SENT,
    FAILED,
    SIGNED,
    RECEIVED,
    REQUESTED,
    REQUEST_EXPIRED,
    REQUEST_CANCELED
} = E_SIGN_STATUSES

const SIGNATURE_STATUS_ICONS = {
    [FAILED]: FileFailed,
    [SENT]: FileRequested,
    [SIGNED]: FileSuccess,
    [RECEIVED]: FileSuccess,
    [REQUESTED]: FileRequested,
    [REQUEST_EXPIRED]: FileExpired,
    [REQUEST_CANCELED]: FileCancelled
}

function DocumentSignatureStatusIcon(
    {
        size,
        hasTip,
        statusName,
        statusTitle,
        className
    }
) {
    const [shouldUpdate, setShouldUpdate] = useState(hasTip)

    const iconRef = useRef()
    const Icon = SIGNATURE_STATUS_ICONS[statusName]

    useEffect(() => {
        if (hasTip && shouldUpdate) {
            setShouldUpdate(false)
        }
    }, [hasTip, shouldUpdate])

    return Icon && (
        <>
            <Icon ref={iconRef} className={className} />
            {hasTip && iconRef.current && (
                <Tooltip target={iconRef.current}>
                    Signature status: {statusTitle}
                </Tooltip>
            )}
        </>
    )
}

DocumentSignatureStatusIcon.defaultProps = {
    hasTip: true
}

export default memo(DocumentSignatureStatusIcon)