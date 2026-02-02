import React, {
    useMemo
} from 'react'

import {
    Col,
    Row
} from 'reactstrap'

import {
    get,
    map,
    chain,
    pluck,
    groupBy
} from 'underscore'

import { useSelectOptions } from 'hooks/common'

import {
    AlertPanel
} from 'components'

import {
    DateField,
    TextField,
    SelectField
} from 'components/Form'

import {
    HIE_CONSENT_POLICIES,
    E_SIGN_DOCUMENT_TEMPLATE_STATUSES
} from 'lib/Constants'

import {
    isEmpty,
    DateUtils as DU
} from 'lib/utils/Utils'

import { isString } from 'lib/utils/StringUtils'
import SelectFieldTree from "../../../../../../../components/Form/SelectFieldTree/SelectField";

const YESTERDAY = DU.startOf(
    DU.add(Date.now(), 1, 'day'), 'day'
).getTime()

export default function BulkSignatureRequestFormFields({
    data,
    errors,

    clients,
    templates:templateOptions,
    communities: communityList,
    organizations,

    isCommunityDisabled,
    isOrganizationDisabled,
    hasNoOrganizationTemplates,

    onChangeField,
    onChangeClient,
    onChangeCommunity,
    onChangeOrganization,
    onChangeExpirationDate,
}) {
    const {
        message,
        communities, // [{communityId, clients: []}]
        templateIds,
        organizationId,
        expirationDate        
    } = data

    const organizationOptions = useSelectOptions(organizations)
    const communityOptions = useSelectOptions(communityList)

    const communityIds = useMemo(() => pluck(communities, 'communityId'),
   // eslint-disable-next-line
        [data])
    const groupedClients = useMemo(() => groupBy(clients, c => c.communityId), [clients])
    return (
        <>
            <Row>
                <Col md={6}>
                    <SelectField
                        name="organizationId"
                        label="Organization*"
                        value={organizationId}
                        options={organizationOptions}
                        onChange={onChangeOrganization}
                        errorText={errors?.organizationId}
                        isDisabled={isOrganizationDisabled}
                    />
                </Col>

                <Col md={6}>
                    <SelectField
                        name="communities"
                        label="Community*"
                        value={communityIds}
                        options={communityOptions}
                        onChange={onChangeCommunity}
                        errorText={isEmpty(communities) && isString(errors?.communities) && errors?.communities}
                        isMultiple
                        isDisabled={isCommunityDisabled}
                    />
                </Col>
            </Row>

            <Row>
                {hasNoOrganizationTemplates && (
                    <Col md={12}>
                        <AlertPanel>
                            There are no organization templates
                        </AlertPanel>
                    </Col>
                )}
                <Col md={6}>
                  {/*  origin one */}
                    <SelectFieldTree
                        name="templateIds"
                        label="Template*"
                        value={templateIds}
                        options={templateOptions}
                        onChange={onChangeField}
                        errorText={errors?.templateIds}
                        isMultiple
                    />
                </Col>

                <Col md={6}>
                    <DateField
                        name="expirationDate"
                        label="Expiration Date*"
                        value={expirationDate}
                        minDate={YESTERDAY}
                        dateFormat="MM/dd/yyyy"                                    
                        placeholder="Select date"
                        errorText={errors?.expirationDate}
                        onChange={onChangeExpirationDate}
                    />
                </Col>
            </Row>
            {map(communities, ({ communityId, clientIds }, index) => (
                <Row key={`clients-${communityId}`}>
                    <Col md={12}>
                        <SelectField
                            name={`communities.${index}.clientIds`}
                            label="Clients*"
                            value={clientIds}
                            hasTags
                            options={map(
                                groupedClients[communityId],
                                c => ({
                                    text: c.fullName,
                                    value: c.id,
                                    isDisabled: !c.primaryContactTypeName
                                        || (c.hieConsentPolicyName === HIE_CONSENT_POLICIES.OPT_OUT && c.primaryContactTypeName === "CARE_TEAM_MEMBER"),
                                    ...!c.primaryContactTypeName && {
                                        tooltip: "To send a signature request, please add or select a primary contact on the Edit Client Screen"
                                    },
                                    ...(c.hieConsentPolicyName === HIE_CONSENT_POLICIES.OPT_OUT && c.primaryContactTypeName === "CARE_TEAM_MEMBER") && {
                                        tooltip: `As a result of your opt out selection, ${c.fullName} no longer has any associated capabilities and can not receive nor complete a signature request.`
                                    }
                                })
                            )}
                            onChange={(_, value) => onChangeClient(value, communityId)}
                            errorText={get(errors, ['communities', index, 'clientIds'])}
                            isMultiple
                            hasSearchBox
                        />
                    </Col>
                </Row>
            ))}
            <Row>
                <Col md={12}>
                    <TextField
                        type="textarea"
                        name="message"
                        value={message}
                        label="Message"
                        onChange={onChangeField}
                        errorText={errors?.message}
                        maxLength={256}
                        numberOfRows={5}
                    />
                </Col>
            </Row>
        </>
    )
}