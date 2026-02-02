import React, {
    memo,
    useCallback
} from 'react'

import cn from 'classnames'

import DocumentTitle from 'react-document-title'

import { Link } from 'react-router-dom'

import { Col, Row } from 'reactstrap'

import YouTube from 'react-youtube'

import { useToggle } from 'hooks/common'

import { usePaperlessHealthcareDemoRequestSubmit } from 'hooks/business/paperless-healthcare'

import { Footer } from 'components'
import { Button } from 'components/buttons'
import { SuccessDialog } from 'components/dialogs'

import { path } from 'lib/utils/ContextUtils'
import { FEATURE_TITLES } from "lib/Constants"

import './SectionDetails.scss'

function SectionDetails(
    {
        name,
        Icon,
        title,
        youtubeVideoId,
        children,
        className,
        onClose
    }
) {
    const [isSuccessDialogOpen, toggleSuccessDialog] = useToggle();

    const { mutateAsync: requestDemo } = usePaperlessHealthcareDemoRequestSubmit({
        onSuccess: () => {
            toggleSuccessDialog();
        }
    });

    const onDemo = useCallback(
        () => requestDemo(FEATURE_TITLES[name]),
        [requestDemo, name]
    );


    return (
        <DocumentTitle title={`Simply Connect | ${title}`}>
            <>
                <div className={cn('SectionDetails', className)}>
                    <div className="SectionDetails-Header">
                        <div className="SectionDetails-Title">
                            <div className="SectionDetails-TitleText">
                                {title}
                            </div>
                        </div>
                    </div>
                    <Row className="SectionDetails-Segments">
                        <Col lg={4} md={6} className="SectionDetails-Segment">
                            {children}
                            <div className="SectionDetails-Actions">
                                <Link
                                    replace
                                    to={path('/paperless-healthcare')}
                                    className="btn btn-outline-success btn-size-regular SectionDetails-Action"
                                >
                                    Close
                                </Link>
                                <Button
                                    color="success"
                                    onClick={onDemo}
                                    className="btn-size-regular SectionDetails-Action"
                                >
                                    Get a Demo
                                </Button>
                            </div>
                        </Col>
                        <Col lg={8} md={6} className="SectionDetails-Segment">
                            {youtubeVideoId && (
                                <YouTube
                                    videoId={youtubeVideoId}
                                    className="SectionDetails-Video"
                                    containerClassName="SectionDetails-VideoContainer"
                                />
                            )}
                        </Col>
                    </Row>
                </div>
                <Footer theme="gray" />
                {isSuccessDialogOpen && (
                    <SuccessDialog
                        isOpen
                        title="Thank you for submitting your request.
                     Our team will get back to you within one business day"
                        buttons={[
                            {
                                text: 'Close',
                                outline: true,
                                onClick: toggleSuccessDialog
                            },
                        ]}
                    />
                )}
            </>
        </DocumentTitle>
    )
}

export default memo(SectionDetails)