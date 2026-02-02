import React from 'react'

import { useHistory } from 'react-router-dom'

import { Loader } from 'components'

import { useScrollable } from 'hooks/common'

import CommunitySummary from '../CommunitySummary/CommunitySummary'

import { hyphenate } from 'lib/utils/Utils'

import { map } from 'lib/utils/ArrayUtils'

import { path } from 'lib/utils/ContextUtils'

import './SavedCommunitiesList.scss'

const scrollableStyles = { flex: 1 }

function SavedCommunityList(
    {
        data,
        isFetching,
        onDelete,
        onCreateReferral
    }
) {
    const history = useHistory()

    const { Scrollable } = useScrollable()

    function viewDetails(community) {
        history.push(path(
            `marketplace/communities/${hyphenate(community.communityName)}--@id=${community.communityId}`
        ))
    }

    return (
        <div className="SavedCommunityList">
            <Scrollable
                style={scrollableStyles}
                className="SavedCommunityList-Scrollable"
            >
                {isFetching && (
                    <Loader hasBackdrop/>
                )}

                {map(data, community => {
                    return (
                        <div
                            key={community.communityId}
                            className="SavedCommunityList-Item"
                        >
                            <CommunitySummary
                                data={community}
                                onDelete={onDelete}
                                onViewDetails={viewDetails}
                                onCreateReferral={onCreateReferral}
                            />
                        </div>
                    )
                })}
            </Scrollable>
        </div>
    )
}

export default SavedCommunityList
