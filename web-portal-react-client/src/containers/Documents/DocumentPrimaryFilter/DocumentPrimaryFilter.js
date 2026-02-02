import React, {
    useEffect
} from 'react'

import cn from 'classnames'
import { TPrimaryFilter } from 'types'

import { first } from 'underscore'

import {
    useAuthUser
} from 'hooks/common/redux'

import {
    useDocumentPrimaryFilterDirectory
} from 'hooks/business/documents'

import { PrimaryFilter } from 'components'

import { isNotEmpty } from 'lib/utils/Utils'
import { PROFESSIONAL_SYSTEM_ROLES } from 'lib/Constants'

import './DocumentPrimaryFilter.scss'

export const NAME = 'DOCUMENT_PRIMARY_FILTER'

export default function DocumentPrimaryFilter(
    {
        data,
        isSaved,
        changeOrganizationField,
        changeCommunityField,
        className,
        ...props
    }
) {
    const {
        organizationId, communityId
    } = data

    const user = useAuthUser()

    const {
        communities,
        organizations
    } = useDocumentPrimaryFilterDirectory({ organizationId })

    useEffect(() => {
        if (user && !communityId) {
            if (organizationId === user.organizationId) {
                changeCommunityField(user.communityId, false)
            } else if (isNotEmpty(communities)) {
                const community = first(communities)
                changeCommunityField(community.id, false)
            }
        }
    }, [
        user,
        communities,
        communityId,
        organizationId,
        changeCommunityField
    ])

    return user && PROFESSIONAL_SYSTEM_ROLES.includes(user.roleName) && (
        <PrimaryFilter
            communities={communities}
            organizations={organizations}
            isCommunityMultiSelection={false}
            data={{ organizationId, communityId }}
            onChangeOrganizationField={changeOrganizationField}
            onChangeCommunityField={changeCommunityField}
            className={cn('DocumentPrimaryFilter', className)}
            {...props}
        />
    )
}

DocumentPrimaryFilter.propTypes = TPrimaryFilter