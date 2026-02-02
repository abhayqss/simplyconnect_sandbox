import React, {
    useState,
    useEffect
} from 'react'

import { map } from 'underscore'

import { Button } from 'reactstrap'

import { useAppointmentQuery } from 'hooks/business/appointments'

import Tabs from 'components/Tabs/Tabs'
import Modal from 'components/Modal/Modal'
import Dropdown from 'components/Dropdown/Dropdown'

import { isInteger } from 'lib/utils/Utils'

import AppointmentDetails from './AppointmentDetails'
import AppointmentChangeHistory from './AppointmentChangeHistory/AppointmentChangeHistory'

import './AppointmentViewer.scss'
const TAB = {
    DETAILS: 0,
    HISTORY: 1,
}

const TAB_TITLE = {
    [TAB.DETAILS]: 'Appointment',
    [TAB.HISTORY]: 'Change history',
}

function getTabs(current) {
    return map(TAB, value => ({
        title: TAB_TITLE[value],
        isActive: +value === current
    }))
}

function AppointmentViewer({
    isOpen,
    onEdit,
    onClose,
    onCancel,
    readOnly = false,
    onDuplicate,
    appointmentId,
    historyEnabled = true
}) {

    const {
        data: appointment
    } = useAppointmentQuery(
        { id: appointmentId },
        {
            enabled: isInteger(appointmentId),
            staleTime: 0
        }
    )

    let [tab, setTab] = useState(TAB.DETAILS)

    useEffect(() => {
        if (!isOpen) setTab(TAB.DETAILS)
    }, [isOpen])

    return (
        <Modal
            isOpen={isOpen}
            onClose={onClose}
            hasCloseBtn={false}
            title="View Appointment"
            className="AppointmentViewer"
            renderFooter={() => (
                <div className='AppointmentViewer-Buttons'>
                    <Button
                        outline
                        color="success"
                        className="width-100"
                        onClick={onClose}
                    >
                        Close
                    </Button>
                    {appointment?.canCancel && !readOnly && (
                        <Button
                            outline
                            color="success"
                            onClick={() => onCancel(appointment)}
                        >
                            Cancel Appointment
                        </Button>
                    )}
                    {appointment?.canDuplicate && !readOnly && (
                        <Button
                            outline
                            color="success"
                            onClick={() => onDuplicate(appointment)}
                        >
                            Duplicate Appointment
                        </Button>
                    )}
                    {appointment?.canEdit && !readOnly && (
                        <Button
                            color="success"
                            onClick={() => onEdit(appointment)}
                        >
                            Edit Appointment
                        </Button>
                    )}
                </div>
            )}
        >
            {historyEnabled && (
                <>
                    <Tabs
                        className="AppointmentViewer-Tabs margin-top-20"
                        items={getTabs(tab)}
                        onChange={setTab}
                    />
                </>
            )}

            {tab === TAB.DETAILS && (
                <AppointmentDetails id={appointmentId} />
            )}

            {historyEnabled && tab === TAB.HISTORY && (
                <AppointmentChangeHistory
                    readOnly={readOnly}
                    appointmentId={appointmentId}
                    onDuplicateAppointment={onDuplicate}
                />
            )}
        </Modal>
    )
}

export default AppointmentViewer
