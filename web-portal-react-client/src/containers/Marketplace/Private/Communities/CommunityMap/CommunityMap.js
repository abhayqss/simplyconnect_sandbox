import React, {
    useMemo,
    useState,
    useEffect,
    useCallback
} from 'react'

import {
    each,
    find,
    isEqual
} from 'underscore'

import {
    useQueryClient,
    QueryClientProvider
} from '@tanstack/react-query'

import {
    Map,
    Loader,
    ErrorViewer,
} from 'components'

import {
    SuccessDialog,
    ConfirmDialog,
} from 'components/dialogs'

import {
    useEventEmitter
} from 'hooks/common'

import {
    useCommunityLocationsQuery
} from 'hooks/business/Marketplace'

import { isValidCoordinate } from 'lib/utils/GeoUtils'

import { ReactComponent as Warning } from 'images/alert-yellow.svg'

import {
    AppointmentScheduler,
    CommunityDescription
} from '../index'

import './CommunityMap.scss'

function CommunityMap(
    {
        filter,
        clientId,
        defaultRegion,
        onViewCommunity,
        programSubTypeId,
        shouldCancelClient
    }
) {
    const [error, setError] = useState(null)
    const [selected, setSelected] = useState(false)

    const [isAppointmentSchedulerOpen, toggleAppointmentScheduler] = useState(false)
    const [isScheduleAppointmentSuccessDialogOpen, toggleScheduleAppointmentSuccessDialog] = useState(false)
    const [isScheduleAppointmentCancelConfirmDialogOpen, toggleScheduleAppointmentCancelConfirmDialog] = useState(false)

    const emitter = useEventEmitter()

    const queryClient = useQueryClient()

    const {
        data: locations = [],
        mutateAsync: fetchLocations,
        isLoading: isFetchingLocations
    } = useCommunityLocationsQuery(
        { ...filter },
        {
            staleTime: 0,
            onError: setError
        }
    )

    const markers = useMemo(() => {
        const markers = []

        each(locations, o => {
            const coordinate = {
                lat: o.location.latitude,
                lng: o.location.longitude
            }

            if (isValidCoordinate(coordinate)) {
                const marker = find(markers, m => isEqual(
                    coordinate, m.coordinate
                ))

                if (marker) {
                    marker.data.communityIds.push(o.communityId)
                    const count = marker.data.communityIds.length
                    marker.label = `${count > 5 ? '5+' : count}`
                } else markers.push({
                    label: '1',
                    coordinate,
                    data: { communityIds: [o.communityId] }
                })
            }
        })

        return markers
    }, [locations])

    const onOpenAppointmentScheduler = useCallback(data => {
        setSelected(data)
        toggleAppointmentScheduler(true)
    }, [])

    function onCloseAppointmentScheduler(shouldConfirm = false) {
        toggleAppointmentScheduler(shouldConfirm)
        toggleScheduleAppointmentCancelConfirmDialog(shouldConfirm)
    }

    function onScheduleAppointmentSuccess() {
        toggleAppointmentScheduler(false)
        toggleScheduleAppointmentSuccessDialog(true)
    }

    function onCloseScheduleAppointmentSuccessDialog() {
        toggleScheduleAppointmentSuccessDialog(false)
    }

    function onCloseScheduleAppointmentCancelConfirmDialog() {
        toggleScheduleAppointmentCancelConfirmDialog(false)
    }

    const renderMapMarkerPopup = useCallback(marker => {
        const filtered = locations?.filter(
            o => marker.data.communityIds.includes(o.communityId)
        ) ?? []

        return (
            <QueryClientProvider client={queryClient}>
                {filtered.map(o => (
                    <CommunityDescription
                        key={o.communityId}
                        communityId={o.communityId}
                        onViewDetails={onViewCommunity}
                        onScheduleAppointment={onOpenAppointmentScheduler}
                    />
                ))}
            </QueryClientProvider>
        )
    }, [
        locations,
        queryClient,
        onViewCommunity,
        onOpenAppointmentScheduler
    ])

    const onFetchLocations = useCallback(
        () => fetchLocations(), [fetchLocations]
    )

    useEffect(() => {
        emitter.on('Marketplace.Communities:fetch', onFetchLocations)
        return () => emitter.off('Marketplace.Communities:fetch', onFetchLocations)
    }, [emitter, onFetchLocations])

    return (
        <div className="CommunityMap">
            <Map
                markers={markers}
                defaultRegion={defaultRegion}
                renderMarkerPopup={renderMapMarkerPopup}
            />

            {isFetchingLocations && (
                <Loader isCentered hasBackdrop/>
            )}

            {isAppointmentSchedulerOpen && (
                <AppointmentScheduler
                    isOpen

                    clientId={clientId}
                    programSubTypeId={programSubTypeId}
                    shouldCancelClient={shouldCancelClient}

                    communityId={selected.communityId}
                    communityName={selected.communityName}
                    organizationName={selected.organizationName}
                    primaryFocusIds={selected.primaryFocuses.map(o => o.id)}
                    treatmentServiceIds={selected.services.map(o => o.id)}

                    onClose={onCloseAppointmentScheduler}
                    onScheduleSuccess={onScheduleAppointmentSuccess}
                />
            )}

            {isScheduleAppointmentSuccessDialogOpen && (
                <SuccessDialog
                    isOpen
                    title='The request has been sent'
                    text={`The community staff will reach out to you within 3 business days. In case of an emergency, please contact ${selected.phone}`}
                    buttons={[
                        {
                            text: 'Close',
                            onClick: onCloseScheduleAppointmentSuccessDialog
                        }
                    ]}
                />
            )}

            {isScheduleAppointmentCancelConfirmDialogOpen && (
                <ConfirmDialog
                    isOpen
                    icon={Warning}
                    confirmBtnText="OK"
                    title="The updates will not be saved"
                    onConfirm={onCloseAppointmentScheduler}
                    onCancel={onCloseScheduleAppointmentCancelConfirmDialog}
                />
            )}

            {error && (
                <ErrorViewer
                    isOpen
                    error={error}
                    onClose={() => setError(null)}
                />
            )}
        </div>
    )
}

export default CommunityMap
