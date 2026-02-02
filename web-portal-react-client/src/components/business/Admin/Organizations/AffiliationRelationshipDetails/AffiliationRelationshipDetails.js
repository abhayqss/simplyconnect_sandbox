import React, {
    Fragment
} from 'react'

import { map } from 'underscore'

import { Link } from 'react-router-dom'

import { isEmpty } from 'lib/utils/Utils'
import { path } from 'lib/utils/ContextUtils'

import Detail from '../../../common/Detail/Detail'

import './AffiliationRelationshipDetails.scss'

function OrganizationLink({ organizationId, children }) {
    return (
        <Link
            className="CommunityDetails-OrganizationLink"
            to={path(`/admin/organizations/${organizationId}`)}
        >
            {children}
        </Link>
    )
}

function CommunityLink({ communityId, organizationId, children }) {
    return (
        <Link
            className="CommunityDetails-CommunityLink"
            to={path(`/admin/organizations/${organizationId}/communities/${communityId}`)}
        >
            {children}
        </Link>
    )
}

function CommunityList({ organizationId, communities = [] }) {
    const count = communities.length

    return map(communities, (o, i) => {
        return (
            <Fragment key={o.id}>
                {o.canView ? (
                    <CommunityLink
                        key={o.id}
                        communityId={o.id}
                        organizationId={organizationId}
                    >
                        {o.name}
                    </CommunityLink>
                ) : o.name}
                {count > 1 && i < (count - 1) && (
                    <span>, </span>
                )}
            </Fragment>
        )
    })
}

function OrganizationCommunities({ communities = [], organization = {} }) {
    const count = communities.length

    return (
        <>
            {count === 0 ? 'All communities' : (
                <>
                    <CommunityList
                        organizationId={organization.id}
                        communities={communities}
                    />&nbsp;
                    {count > 1 ? 'communities' : 'community'}
                </>
            )} from {organization.canView ? (
            <OrganizationLink
                organizationId={organization.id}
            >
                {organization.name}
            </OrganizationLink>
        ) : organization.name} organization
        </>
    )
}

export function AffiliatedCommunitiesDetail(
    {
        data,
        title,
        organizationId,

        className,
        titleClassName,
        valueClassName
    }
) {
    return (
        <Detail
            title={title}
            className={className}
            titleClassName={titleClassName}
            valueClassName={valueClassName}
        >
            {map(data, o => {
                const count = o.communities.length

                return (
                    <div key={o.organization.id} className="AffiliatedCommunityGroup">
                        <OrganizationCommunities
                            communities={o.communities}
                            organization={o.organization}
                        />&nbsp;
                        {o.ownCommunities && (
                            <>
                                {(count === 0 || count > 1) ? 'are' : 'is'} affiliated with&nbsp;
                                {isEmpty(o.ownCommunities) ? (
                                    'all communities of the current organization'
                                ) : (
                                    <>
                                        <CommunityList
                                            communities={o.ownCommunities}
                                            organizationId={organizationId}
                                        />{o.ownCommunities.length > 1 && (<>&nbsp;communities</>)}
                                    </>
                                )}
                            </>
                        )}
                    </div>
                )
            })}
        </Detail>
    )
}

export function PrimaryCommunitiesDetail(
    {
        data,
        title,
        organizationId,

        className,
        titleClassName,
        valueClassName
    }
) {
    return (
        <Detail
            title={title}
            className={className}
            titleClassName={titleClassName}
            valueClassName={valueClassName}
        >
            {map(data, o => {
                const count = o.communities.length

                return (
                    <div key={o.organization.id} className="PrimaryCommunityGroup">
                        <OrganizationCommunities
                            communities={o.communities}
                            organization={o.organization}
                        />&nbsp;
                        {o.ownCommunities && (
                            <>
                                {(count === 0 || count > 1) ? 'are primary for' : 'is a primary one for'}&nbsp;
                                {isEmpty(o.ownCommunities) ? (
                                    'all communities of the current organization'
                                ) : (
                                    <>
                                        <CommunityList
                                            communities={o.ownCommunities}
                                            organizationId={organizationId}
                                        />{o.ownCommunities.length > 1 && (<>&nbsp;communities</>)}
                                    </>
                                )}
                            </>
                        )}
                    </div>
                )
            })}
        </Detail>
    )
}