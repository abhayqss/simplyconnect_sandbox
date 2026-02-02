import React from 'react'

import DocumentTitle from 'react-document-title'

import {
    Row, Col
} from 'reactstrap'

import {
    Button
} from 'components/buttons'

import {
    useNotifyLoginForm,
    useCanLoginToNotify
} from 'hooks/business/notify'

import notifyImgSrc from 'images/hws/notify.jpg'

import './Notify.scss'

export default function Notify() {
    const canLogin = useCanLoginToNotify()

    const [submitLoginForm, loginForm] = useNotifyLoginForm()

    return (
        <DocumentTitle title="Simply Connect | Notify">
            <div className="Notify">
                <div className="Notify-Header">
                    <div className="Notify-Title">
                        Notify
                    </div>
                </div>
                <div className="Notify-Body">
                    <Row>
                        <Col className="col-md-auto padding-bottom-20">
                            <img src={notifyImgSrc} className="Notify-Image"/>
                        </Col>
                        <Col md={6} className="padding-bottom-20">
                            <div className="Notify-Description">
                                NOTIFY Nurse Call is a next generation mobile nurse call solution that allows your caregivers
                                to receive and respond to alerts from wherever they are in the building. Give your staff the
                                tools they need to provide optimal care — and give your senior community the real-time data
                                needed to staff appropriately, assess performance and improve accountability. Utilizing
                                world-renowned, non-proprietary Inovonics technology, NOTIFY is a complete wireless alert system
                                solution that provides real-time communication across the full spectrum of your operation.
                                <div className="Notify-Actions">
                                    {canLogin && (
                                        <Button
                                            color='success'
                                            className="Notify-LoginAction"
                                            onClick={submitLoginForm}
                                        >
                                            Login to Notify
                                        </Button>
                                    )}
                                </div>
                            </div>
                        </Col>
                    </Row>
                </div>
                {loginForm}
            </div>
        </DocumentTitle>
    )
}