import React, { memo } from 'react'

import SelectField from 'containers/TreatmentService/TreatmentServiceSelectField'

import { useDirectoryData } from 'hooks/common'

function TreatmentServiceSelectField(props) {
    const { services } = useDirectoryData({
        services: ['treatment', 'service'],
    })

    return (
        <SelectField
            {...props}
            services={services}
         />
    )
}

export default memo(TreatmentServiceSelectField)