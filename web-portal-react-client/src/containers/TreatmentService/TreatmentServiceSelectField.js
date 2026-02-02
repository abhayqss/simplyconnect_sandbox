import React, { memo, useMemo, useCallback } from 'react'
import { map,  sortBy, groupBy } from 'underscore'

import SelectField from 'components/Form/SelectField/SelectField'

function TreatmentServiceSelectField({ onChange, services, isRequired, ...props }) {
    const sections = useMemo(() => {
        return sortBy(map(
            groupBy(services, 'serviceCategoryId'),
            (data, id) => ({
                id: +id,
                title: data[0].serviceCategoryTitle,
                options: map(data, o => ({
                    value: o.id,
                    text: o.title
                }))
            })
        ), 'title')
    }, [services])

    const onChangeField = useCallback((field, value) => {
        onChange(field, value)
    }, [onChange])

    return (
        <SelectField
            isMultiple
            isSectioned
            hasValueTooltip
            hasSearchBox
            label={`${isRequired ? 'Services*' : 'Services'}`}
            placeholder="Select"
            hasSectionTitle
            hasSectionSeparator
            sections={sections}
            onChange={onChangeField}
            {...props}
        />
    )
}

export default memo(TreatmentServiceSelectField)