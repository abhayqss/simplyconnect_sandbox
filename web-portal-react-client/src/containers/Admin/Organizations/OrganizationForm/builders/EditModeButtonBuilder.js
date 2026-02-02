import React from 'react'

import { STEP } from '../Constants'

import BaseButtonStrategy from './BaseButtonBuilder'

class EditModeButtonBuilder extends BaseButtonStrategy {
    build() {
        const {
            tab,
            buttons,
            permissions
        } = this.context

        const { 
            CloseButton,
            BackButton,
            NextButton,
            SaveButton,
        } = buttons

        return () => (
            <>
                {tab === STEP.LEGAL_INFO && CloseButton}
                {tab !== STEP.LEGAL_INFO && BackButton}

                {tab !== STEP.AFFILIATE_RELATIONSHIP
                    && permissions.canEditAffiliateRelationships
                    && NextButton}
                {tab !== STEP.FEATURES
                    && permissions.canEditFeatures
                    && !permissions.canEditAffiliateRelationships
                    && NextButton}
                {tab !== STEP.MARKETPLACE
                    && !permissions.canEditFeatures
                    && !permissions.canEditAffiliateRelationships
                    && NextButton}

                {tab === STEP.AFFILIATE_RELATIONSHIP
                    && permissions.canEditAffiliateRelationships
                    && SaveButton}
                {tab === STEP.FEATURES
                    && !permissions.canEditAffiliateRelationships
                    && SaveButton}
                {tab === STEP.MARKETPLACE
                    && !permissions.canEditFeatures
                    && !permissions.canEditAffiliateRelationships
                    && SaveButton}
            </>
        )
    }
}

export default EditModeButtonBuilder
