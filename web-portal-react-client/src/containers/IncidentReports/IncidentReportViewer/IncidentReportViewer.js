import React from 'react'

import { Button } from 'reactstrap'

import Modal from 'components/Modal/Modal'

import IncidentReportDetails from '../IncidentReportDetails/IncidentReportDetails'

import './IncidentReportViewer.scss'

function IncidentReportViewer({ isOpen, reportId, onClose }) {
    return (
        <Modal
            isOpen={isOpen}
            onClose={onClose}
            hasCloseBtn={false}
            title="View Incident Report"
            className="IncidentReportViewer"
            renderFooter={() => (
                <Button
                    color="success"
                    onClick={onClose}
                >
                    Close
                </Button>
            )}
        >
            <IncidentReportDetails
                reportId={reportId}
                hasActions={false}
                hasBreadcrumbs={false}
            />
        </Modal>
    )
}

export default IncidentReportViewer