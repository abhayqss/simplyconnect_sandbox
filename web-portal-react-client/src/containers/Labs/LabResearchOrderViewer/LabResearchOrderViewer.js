import React from 'react'

import { Button } from 'reactstrap'

import Modal from 'components/Modal/Modal'

import LabResearchOrderDetails from '../LabResearchOrderDetails/LabResearchOrderDetails'

import './LabResearchOrderViewer.scss'

function LabResearchOrderViewer({ isOpen, onClose, orderId }) {
    return (
        <Modal
            isOpen={isOpen}
            onClose={onClose}
            hasCloseBtn={false}
            title="View Order"
            className="LabResearchOrderViewer"
            renderFooter={() => (
                <Button
                    color="success"
                    onClick={onClose}
                >
                    Close
                </Button>
            )}
        >
            <LabResearchOrderDetails id={orderId} />
        </Modal>
    )
}

export default LabResearchOrderViewer