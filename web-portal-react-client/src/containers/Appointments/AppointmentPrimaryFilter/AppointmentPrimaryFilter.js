import React, { useCallback } from 'react'

import cn from 'classnames'

import { PrimaryFilter } from 'components'

import {
    useAppointmentPrimaryFilterDirectory,
	useAppointmentPrimaryFilterInitialization
} from 'hooks/business/appointments'

import './AppointmentPrimaryFilter.scss'

export default function AppointmentPrimaryFilter(
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
	} = useAppointmentPrimaryFilterDirectory(
		data, { actions: { changeFilterFields: changeFields } }
	)

	useAppointmentPrimaryFilterInitialization({
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
			className={cn('AppointmentPrimaryFilter', className)}
			data={{ organizationId, communityIds }}
		/>
	)
}