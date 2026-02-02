import React, { memo } from 'react'

import { FEATURES } from 'lib/Constants'

import imageSrc from 'images/hws/clinical-pharmacist.jpg'

import SectionTile from '../components/SectionTile/SectionTile'

const { CLINICAL_PHARMACIST } = FEATURES

function ClinicalPharmacistTile({ onClick }) {
    return (
        <SectionTile
            name={CLINICAL_PHARMACIST}
            title="Clinical Pharmacist"
            imageSrc={imageSrc}
            onClick={onClick}
        />
    )
}

export default memo(ClinicalPharmacistTile)