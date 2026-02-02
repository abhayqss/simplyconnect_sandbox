import React, {
    useCallback
} from 'react'

import cn from 'classnames'

import { first } from 'underscore'

import { useHistory } from 'react-router-dom'

import {
    Card,
    CardBody,
} from 'reactstrap'

import {
    Map,
    Picture
} from 'components'

import {
    isEmpty,
    isNotEmpty,
} from 'lib/utils/Utils'

import { path } from 'lib/utils/ContextUtils'

import CommunitySummary from '../CommunitySummary/CommunitySummary'

import './CommunityCard.scss'

function CommunityCard({
    data,
    onRemove,
    className,
    onCreateReferral
}) {
    const history = useHistory()

    const {
        location,
        communityId,
        communityName,
        organizationId
    } = data ?? {}

    function viewDetails() {
        history.push(path(`/marketplace/communities/${communityName}--@id=${communityId}`))
    }

    const marker = {
        isSelected: true,
        coordinate: {
            lat: location?.latitude,
            lng: location?.longitude
        }
    }

    const picture = first(data.pictures)

    const _onRemove = useCallback(
        () => onRemove(data), [data, onRemove]
    )

    return (
        <Card className={cn('CommunityCard', className)}>
            <div className="CommunityCard-Head">
                {isNotEmpty(data?.pictures) && (
                    <Picture
                        name={picture.name}
                        mimeType={picture.mimeType}
                        className="CommunityCard-Photo"
                        path={`/organizations/${organizationId}/communities/${communityId}/pictures/${picture.id}`}
                    />
                )}

                {isEmpty(data?.pictures) && (
                    <Map
                        markers={[marker]}
                        defaultRegion={marker.coordinate}
                    />
                )}
            </div>

            <CardBody className="CommunityCard-Body">
                <CommunitySummary
                    data={data}
                    onDelete={_onRemove}
                    onViewDetails={viewDetails}
                    onCreateReferral={onCreateReferral}
                />
            </CardBody>
        </Card>
    )
}

export default CommunityCard