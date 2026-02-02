import React from 'react'
import EmergencyContactList from '../EmergencyContactList/EmergencyContactList'
import AttorneyDetails from '../AttorneyDetails/AttorneyDetails'

function EmergencyContacts({ clientId }) {
    return (
        <div className='EmergencyContactsContainer'>
            <EmergencyContactList clientId={clientId} />
            <AttorneyDetails />
        </div>
    )
}

export default EmergencyContacts
