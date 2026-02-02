import React from 'react'

import { connect } from 'react-redux'

import { Button } from 'reactstrap'

import { Modal } from 'components'

import { AllergyDetails } from 'containers/common/details'

import './AllergyViewer.scss'

function AllergyViewer({ isOpen, onClose, clientId, allergyId }) {
    return (
        <Modal
            isOpen={isOpen}
            onClose={onClose}
            hasCloseBtn={false}
            title="View Allergy"
            className="AllergyViewer"
            renderFooter={() => (
                <Button color='success' onClick={onClose}>
                    Close
                </Button>
            )}
        >
            <AllergyDetails
                clientId={clientId}
                allergyId={allergyId}
            />
        </Modal>
    )
}

export default connect()(AllergyViewer)