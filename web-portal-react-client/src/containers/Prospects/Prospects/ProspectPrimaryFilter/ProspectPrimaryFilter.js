import React, { useCallback } from 'react'

import cn from 'classnames'

import { PrimaryFilter } from 'components'

import {
    useProspectPrimaryFilterDirectory,
	useProspectPrimaryFilterInitialization
} from 'hooks/business/Prospects'

import './ProspectPrimaryFilter.scss'

export default function ProspectPrimaryFilter(
	{
		data,
		changeFields,
		changeOrganizationField,
		className
	}
) {
	const {
		communityIds,
		organizationId
	} = data

	const {
		communities,
		organizations
	} = useProspectPrimaryFilterDirectory(
		data, { actions: { changeFilterFields: changeFields } }
	)

	useProspectPrimaryFilterInitialization({
		organizationId, communityIds, changeFields
	})

	const onChangeCommunityField = useCallback(value => {
		changeFields({
			communityIds: value,
			excludeWithoutCommunity: true
		}, true)
	}, [changeFields])

	return (
		<PrimaryFilter
			communities={communities}
			organizations={organizations}
			onChangeOrganizationField={changeOrganizationField}
			onChangeCommunityField={onChangeCommunityField}
			className={cn('ProspectPrimaryFilter', className)}
			data={{ organizationId, communityIds }}
		/>
	)
}