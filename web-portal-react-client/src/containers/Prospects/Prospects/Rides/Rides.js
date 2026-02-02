import React, {
    memo,
    useRef,
    useState,
    useEffect,
    useCallback
} from 'react'

import $ from 'jquery'

import { compact } from 'underscore'

import { useParams } from 'react-router-dom'

import DocumentTitle from 'react-document-title'

import { Button } from 'reactstrap'

import {
    useAuthUser,
    useLocationState
} from 'hooks/common'

import {
    useProspectQuery,
    useSideBarUpdate,
} from 'hooks/business/Prospects'

import {
    useProspectRideHistoryQuery,
    useProspectRideRequestQuery
} from 'hooks/business/Prospects/Rides'

import {
    Breadcrumbs
} from 'components'

import {
    Dialog,
    WarningDialog
} from 'components/dialogs'

import ProspectEditor from '../ProspectEditor/ProspectEditor'

import {
    allAreNotEmpty,
} from 'lib/utils/Utils'
import { Response } from 'lib/utils/AjaxUtils'

import { isDataValid } from '../ProspectDashboard/ProspectDashboard'

import {
    TRANSPORTATION_ACTION,
    TRANSPORTATION_ACTION_DESC
} from 'lib/Constants'

import './Rides.scss'

function Rides() {
    const { prospectId } = useParams()

    const [{
        isInstructionNeed
    } = {}, clearLocationState] = useLocationState()

    const [isEditorOpen, setIsEditorOpen] = useState(false)
    const [transportationAction, setTransportationAction] = useState(null)
    const [isInvalidDataWarningDialogOpen, setIsInvalidDataWarningDialogOpen] = useState(false)
    const [isInstructionDialogOpen, toggleInstructionDialog] = useState(isInstructionNeed)

    const transportationFormRef = useRef()

    const {
        data: prospect = {}
    } = useProspectQuery({ prospectId })

    const { canRequestRide, canViewRideHistory } = prospect

    const user = useAuthUser()

    const {
        fetch: createRideRequest
    } = useProspectRideRequestQuery(
        { prospectId },
        { staleTime: 0, enabled: false }
    )

    const {
        fetch: fetchRideHistory
    } = useProspectRideHistoryQuery(
        { prospectId },
        { staleTime: 0, enabled: false })

    function submitTransportationForm(url, token, action = '') {
        if (allAreNotEmpty(url, token)) {
            const form = (
                transportationFormRef.current
            )

            $(form).attr('action', url)

            $(form).find('[name="payload"]').val(token)
            $(form).find('[name="action"]').val(action)

            form.submit()
        }
    }

    const onCreateTransportationRideRequest = useCallback(() => {
        createRideRequest({ prospectId })
            .then(Response(({ data: { url, token } = {} }) => {
                submitTransportationForm(url, token, 'create')
            }))
    }, [prospectId, createRideRequest])

    const onOpenTransportationRideHistory = useCallback(() => {
        fetchRideHistory({ prospectId })
            .then(Response(({ data: { url, token } = {} }) => {
                submitTransportationForm(url, token)
            }))
    }, [prospectId, fetchRideHistory])

    function requestRide() {
        setTransportationAction(TRANSPORTATION_ACTION.RIDE)

        if (isDataValid(prospect) && (prospect.email || user?.email)) {
            onCreateTransportationRideRequest()
        } else {
            setIsInvalidDataWarningDialogOpen(true)
        }
    }

    function viewHistory() {
        setTransportationAction(TRANSPORTATION_ACTION.HISTORY)

        if (isDataValid(prospect) && (prospect.email || user?.email)) {
            onOpenTransportationRideHistory()
        } else {
            setIsInvalidDataWarningDialogOpen(true)
        }
    }

    const updateSideBar = useSideBarUpdate({ prospectId })

    const onCloseEditor = useCallback(() => {
        setIsEditorOpen(false)
        setTransportationAction(null)
    }, [])

    useProspectQuery({ prospectId })

    useEffect(updateSideBar, [])

    const isProspectActive = prospect?.isActive;

    return (
        <DocumentTitle title="Simply Connect | Prospects | Rides">
            <div className="ProspectRides">
                <div className="ProspectRides-Header">
                    <Breadcrumbs
                        className="ProspectRides-Breadcrumbs"
                        items={compact([
                            {
                                title: 'Prospects',
                                href: '/prospects',
                                isEnabled: true
                            },
                            {
                                title: prospect.fullName ?? '',
                                href: `/prospects/${prospectId}`,
                                isEnabled: true
                            },
                            {
                                title: 'Rides',
                                isActive: true
                            },
                        ])}
                    />

                    <div className="ProspectRides-Title">Rides</div>
                </div>

                <div className="ProspectRides-Body">
                    <div className="ProspectRides-MessageText">
                        <p>
                            With Simply Connect Transportation you can efficiently schedule safe and
                            reliable rides directly from your laptop, tablet or phone.
                        </p>

                        <p>
                            Simply Connect is integrated real-time directly into the providers’ dispatch system.
                            Requesting, updating or canceling transportation appointments is electronic, seamless and does
                            not require additional faxes or phone calls. You are provided with ride confirmation instantly
                            and can easily track the ride history.
                        </p>

                        <p>
                            Simply Connect connected to fleets nationwide and continues to grow. All fleets and drivers
                            are trained and certified, this is your instant source of certified medical transportation providers.
                        </p>
                    </div>

                    <div className="ProspectRides-ActionButtons">
                        <Button
                            outline
                            color="success"
                            className="ProspectRides-Button ProspectRides-ViewHistoryBtn"
                            disabled={!canViewRideHistory}
                            onClick={viewHistory}
                        >
                            Ride History
                        </Button>

                        {isProspectActive && <Button
                            color="success"
                            className="ProspectRides-Button ProspectRides-RequestRideBtn"
                            disabled={!canRequestRide}
                            onClick={requestRide}
                        >
                            Request a Ride
                        </Button> }
                    </div>
                </div>

                <ProspectEditor
                    isOnDashboard
                    isOpen={isEditorOpen}

                    prospectId={prospectId}
                    // isClientEmailRequired={(
                    //     isNotEmpty(transportationAction)
                    //     && !(client.email || client?.email)
                    // )}

                    // isValidationNeed={isNotEmpty(transportationAction)}

                    onClose={onCloseEditor}
                    onSaveSuccess={onCloseEditor}
                />

                {isInvalidDataWarningDialogOpen && (
                    <WarningDialog
                        isOpen
                        title={`Please fill in the required fields to ${TRANSPORTATION_ACTION_DESC[transportationAction]}`}
                        buttons={[
                            {
                                text: 'Cancel',
                                outline: true,
                                onClick: () => {
                                    setTransportationAction(null)
                                    setIsInvalidDataWarningDialogOpen(false)
                                }
                            },
                            {
                                text: 'Edit Record',
                                onClick: () => {
                                    setIsEditorOpen(true)
                                    setIsInvalidDataWarningDialogOpen(false)
                                }
                            }
                        ]}
                    />
                )}

                {isInstructionDialogOpen && (
                    <Dialog
                        isOpen
                        buttons={[
                            {
                                text: 'Close',
                                color: 'success',
                                onClick: () => {
                                    clearLocationState()
                                    toggleInstructionDialog(false)
                                }
                            }
                        ]}
                    >
                        <p>Easily schedule non-emergency transportation services and share details with family members and care teams by clicking Request Ride button.</p>
                        <p>If you have any operational questions reach out to our Support team: <b>support@simplyconnect.me</b></p>
                    </Dialog>
                )}

                <form
                    method="POST"
                    target="_blank"
                    className="d-none"
                    ref={transportationFormRef}
                >
                    <input name="action" />
                    <input name="payload" />
                </form>
            </div>
        </DocumentTitle>
    )
}

export default memo(Rides)
