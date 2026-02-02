import React from 'react'
import AttorneyDetails from '../AttorneyDetails/AttorneyDetails'
import FreshEmergencyContactList from "../FreshEmergencyContactList/FreshEmergencyContactList";

function FreshEmergencyContacts({ clientId }) {
    return (
        <div className='EmergencyContactsContainer'>
            <FreshEmergencyContactList clientId={clientId} />
        </div>
    )
}

export default FreshEmergencyContacts
