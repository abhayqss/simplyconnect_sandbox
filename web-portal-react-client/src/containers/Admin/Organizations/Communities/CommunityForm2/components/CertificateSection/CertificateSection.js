import React from 'react'

import { Row, Col } from 'reactstrap'

import moment from 'moment'

import { AlertPanel } from 'components'
import { SwitchField, DropzoneField } from 'components/Form'

import { ALLOWED_FILE_FORMATS, ALLOWED_FILE_FORMAT_MIME_TYPES } from 'lib/Constants'

const { CER, CERT, CRT } = ALLOWED_FILE_FORMATS

const ALLOWED_CERTIFICATE_MIME_TYPES = [CER, CERT, CRT].map(
    type => ALLOWED_FILE_FORMAT_MIME_TYPES[type]
)

const Certificate = (certificate) => (
    <AlertPanel className="CommunityForm-AlertPanel">
        <p>Issued to: {certificate.subject}</p>
        <p>Issued by: {certificate.issuer}</p>
        <p>Valid to: {moment(certificate.expiresAt).format('MM/DD/YYYY')}</p>
        <p>Thumbprint: {certificate.sha1Fingerprint}</p>
    </AlertPanel>
)

function CertificateSection({ config, onChange }) {
    const {
        serverCertificate,
        configuredCertificate,

        publicKeyCertificates,
        publicKeyCertificatesErrorText,

        useSuggestedCertificate,
    } = config

    return (
        <>
            <div className="CommunityForm-SectionHeader">
                <div className="CommunityForm-SectionTitle">
                    Public Key Certificate
                </div>
            </div>

            {serverCertificate === null && configuredCertificate === null && (
                <Row>
                    <Col>
                        <AlertPanel>
                            Certificate is signed and trusted
                        </AlertPanel>
                    </Col>
                </Row>
            )}

            {serverCertificate === null && configuredCertificate !== null && (
                <Row>
                    <Col>
                        <AlertPanel className="CommunityForm-AlertPanel">
                            <p>The currently uploaded certificate does not match with the server certificate.</p>
                            <p>The configured self-signed certificate will be deleted by clicking on the Save button located on the Marketplace tab.</p>
                            <p>The trusted certificate will be used instead.</p>
                        </AlertPanel>
                    </Col>
                </Row>
            )}

            {serverCertificate !== null && (
                <>
                    <Row>
                        <Col>
                            {configuredCertificate !== null && configuredCertificate.sha1Fingerprint !== serverCertificate.sha1Fingerprint && (
                                <p>
                                    The currently uploaded certificate does not match with the server certificate. Please use the suggested certificate or upload a new one.
                                </p>
                            )}

                            <Certificate {...serverCertificate} />
                        </Col>
                    </Row>

                    <Row>
                        <Col>
                            <SwitchField
                                name="docutrackPharmacyConfig.useSuggestedCertificate"
                                onChange={onChange}
                                isChecked={useSuggestedCertificate}
                                label="Use this certificate"
                                className="CertificateSwitch-SwitchField"
                            />
                        </Col>
                    </Row>
                </>
            )}

            <Row>
                <Col>
                    {!useSuggestedCertificate && (
                        <DropzoneField
                            name="docutrackPharmacyConfig.publicKeyCertificates"
                            label="Public Key Certificate*"
                            value={publicKeyCertificates}
                            maxCount={1}
                            hintText="Supported file types: CER, CRT | Max 50 mb"
                            className="CommunityForm-DropzoneField"
                            errors={publicKeyCertificatesErrorText}
                            allowedTypes={ALLOWED_CERTIFICATE_MIME_TYPES}
                            onChange={onChange}
                        />
                    )}
                </Col>
            </Row>
        </>
    )
}

export default CertificateSection
