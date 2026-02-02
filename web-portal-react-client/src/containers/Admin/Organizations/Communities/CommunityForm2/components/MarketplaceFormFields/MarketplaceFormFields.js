import React, {
    memo,
    useMemo,
    useState,
    useCallback
} from 'react'

import cn from 'classnames'

import {
    chain
} from 'underscore'

import {
    Col,
    Row,
    Button
} from 'reactstrap'

import {
    TextField,
    SelectField,
    CheckboxField
} from 'components/Form'

import { ConfirmDialog } from 'components/dialogs'

import {
    useServicesQuery,
    useServiceCategoriesQuery,
    useMarketplaceLanguagesQuery
} from 'hooks/business/directory/query'

import ReferralEmail from 'entities/ReferralEmail'

import {
    isEmpty
} from 'lib/utils/Utils'

import {
    map
} from 'lib/utils/ArrayUtils'

import { ReactComponent as Info } from 'images/info.svg'
import { ReactComponent as Cross } from 'images/cross.svg'
import { ReactComponent as Warning } from 'images/alert-yellow.svg'

import FeaturedServiceProviderSection from '../FeaturedServiceProviderSection/FeaturedServiceProviderSection'

function valueTextMapper({ id, name, value, label, title }) {
    return { value: id || value || name, text: label || title || name }
}

function MarketplaceFormFields(
    {
        errors,
        fields,
        permissions,
        communityId,
        organizationId,
        onChangeField
    }
) {
    const [selectedEmail, setSelectedEmail] = useState(null)

    const [isConfirmRemovingEmailDialogOpen, setIsConfirmRemovingEmailDialogOpen] = useState(false)

    const serviceIds = useMemo(() => fields.marketplace.serviceIds.toJS(), [fields.marketplace.serviceIds])
    const languageIds = useMemo(() => fields.marketplace.languageIds.toJS(), [fields.marketplace.languageIds])
    const serviceCategoryIds = useMemo(() => fields.marketplace.serviceCategoryIds.toJS(), [fields.marketplace.serviceCategoryIds])

    const {
        data: serviceCategories = []
    } = useServiceCategoriesQuery({}, { staleTime: 0 })

    const mappedServiceCategories = useMemo(
        () => map(serviceCategories, valueTextMapper), [serviceCategories]
    )

    const {
        data: services = []
    } = useServicesQuery({ serviceCategoryIds }, { staleTime: 0 })

    const mappedServices = useMemo(() => (
        chain(serviceCategories)
            .filter(o => serviceCategoryIds.includes(o.id))
            .map(o => ({
                id: o.id,
                title: o.label ?? o.title,
                options: (
                    chain(services)
                        .where({ serviceCategoryId: o.id })
                        .map(valueTextMapper).value()
                )
            })).value()
    ), [
        services,
        serviceCategories,
        serviceCategoryIds
    ])

    const {
        data: marketplaceLanguages = []
    } = useMarketplaceLanguagesQuery({}, { staleTime: 0 })

    const mappedMarketplaceLanguages = useMemo(
        () => map(marketplaceLanguages, valueTextMapper), [marketplaceLanguages]
    )

    function addReferralEmail() {
        onChangeField(
            'marketplace.referralEmails',
            fields.marketplace.referralEmails.push(ReferralEmail())
        )
    }

    function removeReferralEmail(value) {
        let emails = (
            fields.marketplace.referralEmails.filter(
                email => email !== value
            )
        )

        if (emails.size === 0) {
            emails = emails.push(ReferralEmail())
        }

        onChangeField('marketplace.referralEmails', emails)
    }

    function openConfirmRemovingEmailDialog(email) {
        setSelectedEmail(email)
        setIsConfirmRemovingEmailDialogOpen(true)
    }

    function closeConfirmRemovingEmailDialog() {
        setSelectedEmail(null)
        setIsConfirmRemovingEmailDialogOpen(false)
    }

    function tryRemovingReferralEmail(value) {
        if (value.canEdit) {
            removeReferralEmail(value)
        } else {
            openConfirmRemovingEmailDialog(value)
        }
    }

    function confirmRemovingEmail() {
        removeReferralEmail(selectedEmail)
        closeConfirmRemovingEmailDialog()
    }

    const onChangeCategoryField = useCallback((name, value) => {
        onChangeField(name, value)
        onChangeField('marketplace.serviceIds', [])
    }, [onChangeField])

    return (
        <>
            <div className="Marketplace">
                <div className="CommunityForm-Section Marketplace-Section">
                    <Row>
                        <Col lg={6}>
                            <CheckboxField
                                name="marketplace.confirmVisibility"
                                value={fields.marketplace.confirmVisibility}
                                className="CommunityForm-ConfirmVisibilityField"
                                label="Confirm that community will be visible in Marketplace"
                                isDisabled={!permissions.canEditConfirmMarketplaceVisibility}
                                renderLabelIcon={() => (
                                    <Info
                                        id="ConfirmVisibilityHint"
                                        className="CommunityForm-InfoHint ConfirmVisibilityHint"
                                    />
                                )}
                                tooltip={{
                                    placement: 'right',
                                    target: 'ConfirmVisibilityHint',
                                    boundariesElement: document.body,
                                    text: 'The community will be available in the search results in mobile and web apps ("Marketplace" feature).'
                                }}
                                onChange={onChangeField}
                            />
                        </Col>
                        <Col lg={6}>
                            <CheckboxField
                                name="marketplace.allowExternalInboundReferrals"
                                value={fields.marketplace.allowExternalInboundReferrals}
                                className="CommunityForm-AllowInboundRRField"
                                label="Allow referral requests from outside of network"
                                isDisabled={!permissions.canEditAllowExternalInboundReferrals}
                                renderLabelIcon={() => (
                                    <Info
                                        id="AllowInboundRRHint"
                                        className="CommunityForm-InfoHint AllowInboundRRHint"
                                    />
                                )}
                                tooltip={{
                                    placement: 'right',
                                    target: 'AllowInboundRRHint',
                                    boundariesElement: document.body,
                                    text: 'By enabling this checkbox you allow referral requests from outside of network.'
                                }}
                                onChange={onChangeField}
                            />
                        </Col>
                    </Row>

                    <Row>
                        <Col lg={12}>
                            <TextField
                                type="textarea"
                                name="marketplace.servicesSummaryDescription"
                                value={fields.marketplace.servicesSummaryDescription}
                                label="Services Summary Description*"
                                className="CommunityForm-TextAreaField"
                                maxLength={5000}
                                errorText={errors.marketplace?.servicesSummaryDescription}
                                onChange={onChangeField}
                            />
                        </Col>
                    </Row>

                    <Row>
                        <Col lg={6} md={6}>
                            <SelectField
                                isMultiple
                                hasValueTooltip
                                name="marketplace.serviceCategoryIds"
                                value={serviceCategoryIds}
                                options={mappedServiceCategories}
                                label="Category*"
                                className="OrganizationForm-SelectField"
                                errorText={errors.marketplace?.serviceCategoryIds}
                                onChange={onChangeCategoryField}
                            />
                        </Col>
                        <Col lg={6} md={6}>
                            <SelectField
                                isMultiple
                                isSectioned
                                hasValueTooltip
                                hasSectionIndicator
                                hasSectionSeparator
                                name="marketplace.serviceIds"
                                value={serviceIds}
                                sections={mappedServices}
                                label="Services*"
                                placeholder="Select"
                                className="OrganizationForm-SelectField"
                                isDisabled={isEmpty(serviceCategoryIds)}
                                errorText={errors.marketplace?.serviceIds}
                                onChange={onChangeField}
                            />
                        </Col>
                    </Row>

                    <Row>
                        <Col lg={6} md={6}>
                            <SelectField
                                isMultiple
                                hasNoneOption
                                hasValueTooltip
                                name="marketplace.languageIds"
                                value={languageIds}
                                options={mappedMarketplaceLanguages}
                                label="Languages"
                                placeholder="Select"
                                className="OrganizationForm-SelectField"
                                errorText={errors.marketplace?.languageIds}
                                onChange={onChangeField}
                            />
                        </Col>
                    </Row>
                </div>

                <div className="CommunityForm-Section Marketplace-Section">
                    <div className="CommunityForm-SectionTitle CommunityForm-SectionTitle_withButton">
                        Referrals

                        <Button
                            color="success"
                            onClick={addReferralEmail}
                            disabled={!permissions.canEditMarketplaceReferralEmails}
                        >
                            Add Email
                        </Button>
                    </div>

                    <Row>
                        {fields.marketplace.referralEmails?.map((email, index) => {
                            let isFirst = index === 0
                            let label = "Email" + (isFirst ? "*" : "")

                            let canBeRemoved = (
                                (!email.canEdit || !isFirst)
                                && permissions.canEditMarketplaceReferralEmails
                            )

                            return (
                                <Col md={6} key={index}>
                                    <TextField
                                        type="text"
                                        name={`marketplace.referralEmails.${index}.value`}
                                        value={email.value}
                                        label={label}
                                        maxLength={250}
                                        className={cn(
                                            "CommunityForm-TextField",
                                            { "CommunityForm-TextField_withIcon": canBeRemoved },
                                            { "CommunityForm-TextField_withoutError": !email.canEdit }
                                        )}
                                        isDisabled={!(
                                            email.canEdit
                                            && permissions.canEditMarketplaceReferralEmails
                                        )}
                                        errorText={errors.marketplace?.referralEmails?.[index]?.value}
                                        onChange={onChangeField}
                                        renderIcon={() => canBeRemoved && (
                                            <Cross
                                                id="phoneInfoIcon"
                                                className="CommunityForm-CloseIcon"
                                                onClick={() => tryRemovingReferralEmail(email)}
                                            />
                                        )}
                                    />
                                </Col>
                            )
                        })}
                    </Row>
                </div>

                <div className="CommunityForm-Section Marketplace-Section">
                    <FeaturedServiceProviderSection
                        communityId={communityId}
                        canEdit={permissions.canEditFeaturedServiceProviders}
                        isVisible={!!communityId && fields.marketplace.confirmVisibility}
                        communities={fields.marketplace.featuredCommunities.toArray()}
                        organizationId={organizationId}
                        onChange={data => onChangeField('marketplace.featuredCommunities', data)}
                    />
                </div>
            </div>

            {isConfirmRemovingEmailDialogOpen && (
                <ConfirmDialog
                    isOpen
                    icon={Warning}
                    confirmBtnText="Confirm"
                    title="The user will no longer has access to the Referrals submitted to the current community"
                    onConfirm={confirmRemovingEmail}
                    onCancel={closeConfirmRemovingEmailDialog}
                />
            )}
        </>
    )
}

export default memo(MarketplaceFormFields)
