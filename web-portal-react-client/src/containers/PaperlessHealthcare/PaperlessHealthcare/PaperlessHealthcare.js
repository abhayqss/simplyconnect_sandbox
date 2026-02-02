import React, { memo } from 'react'

import cn from 'classnames'

import Title from 'react-document-title'

import {
    useAuthUser
} from 'hooks/common/redux'

import {
    Footer, Picture
} from 'components'

import { camel } from 'lib/utils/Utils'

import Features from '../Features/Features'

import './PaperlessHealthcare.scss'

function PaperlessHealthcare() {
    const user = useAuthUser()

    return (
        <Title title="Simply Connect | Paperless Healthcare">
            <>
                <div className="PaperlessHealthcare">
                    <div className="PaperlessHealthcare-Header">
                        <div className="PaperlessHealthcare-HeaderSegment">
                            <div className="PaperlessHealthcare-Title">
                                Paperless Healthcare
                            </div>
                            {user && (
                                <Picture
                                    className="PaperlessHealthcare-OrganizationLogo"
                                    path={`/organizations/${user?.organizationId}/logo`}
                                />
                            )}
                        </div>
                    </div>
                    <div className="v-flexbox align-items-center">
                        <Features/>
                    </div>
                </div>
                <Footer theme="gray"/>
            </>
        </Title>
    )
}

export default memo(PaperlessHealthcare)