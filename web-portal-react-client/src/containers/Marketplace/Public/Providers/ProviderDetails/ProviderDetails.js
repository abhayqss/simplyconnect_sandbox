import React, {
	memo,
	useState,
	useCallback
} from 'react'

import cn from 'classnames'
import PTypes from 'prop-types'

import {
	map,
	noop,
	compact
} from 'underscore'

import {
	useParams
} from 'react-router-dom'

import ShowMore from 'react-show-more'
import DocumentTitle from 'react-document-title'

import { Row, Col } from 'reactstrap'

import {
	Footer,
	Picture,
	Carousel,
	Breadcrumbs,
	ErrorViewer,
	DataLoadable,
	CollapsibleSection
} from 'components'

import {
	Button
} from 'components/buttons'

import {
	useSubDomain
} from 'hooks/common'

import {
	useProviderQuery,
	useOrganizationQuery
} from 'hooks/business/Marketplace/Public'

import { ReactComponent as Bed } from 'images/bed-sleep.svg'
import { ReactComponent as Location } from 'images/location.svg'
import { ReactComponent as Checkmark } from 'images/check-mark-4.svg'
import { ReactComponent as BedPulse } from 'images/bed-sleep-pulse.svg'
import { ReactComponent as ChevronTop } from 'images/chevron-top.svg'
import { ReactComponent as ChevronBottom } from 'images/chevron-bottom.svg'

import {
	isNotEmpty,
	hyphenatedToTitle
} from 'lib/utils/Utils'

import { ProviderHeader } from '../'
import { InquiryEditor } from '../Inquiry'

import './ProviderDetails.scss'

function MarketplaceProviderDetails() {
	const [error, setError] = useState(null)
	const [isInquiryEditorOpen, toggleInquiryEditorOpen] = useState(false)

	const params = useParams()
	const organizationCode = useSubDomain()

	const providerId = params.communityId
	const providerName = params.communityName

	const {
		data,
		refetch,
		isFetching
	} = useProviderQuery(
		{ providerId },
		{
			staleTime: 0,
			onError: setError
		}
	)

	const {
		data: organization
	} = useOrganizationQuery({ organizationCode })

	const onCloseInquiryEditor = useCallback(() => {
		toggleInquiryEditorOpen(false)
	}, [])

	const onSaveInquirySuccess = useCallback(() => {
		toggleInquiryEditorOpen(false)
		refetch()
	}, [refetch])

	return (
		<DocumentTitle
			title={`${organization?.loginCompanyId} | Simply Connect | Community Details | ${hyphenatedToTitle(providerName)}`}
		>
			<div className="MarketplaceProviderDetails">
				<ProviderHeader/>

				<div className="MarketplaceProviderDetails-Body">
					<Breadcrumbs
						className="margin-top-15 margin-left-24 margin-bottom-15"
						items={compact([
							{
								title: 'Marketplace',
								href: '/',
								isEnabled: true
							},
							{
								title: 'Community Details',
								href: `/marketplace/communities/${providerName}--@id=${providerId}`,
								isActive: true
							}
						])}
					/>
					<DataLoadable
						data={data}
						isLoading={isFetching}
						loaderStyle={{ marginTop: '70%' }}
					>
						{data => {
							return (
								<div className="d-flex flex-row justify-content-center">
									<div className="MarketplaceProviderDetails-Section">
										<div className="MarketplaceProviderDetails-Title">
											{data.name}
										</div>

										<div className="MarketplaceProviderDetails-OrganizationName">
											{data.organizationName}
										</div>

										{isNotEmpty(data.serviceCategories) && (
											<div className="MarketplaceProviderDetails-ServiceCategories">
												{map(data.serviceCategories, o => (
													<div className="MarketplaceProviderDetails-ServiceCategory">
														{o.title}
													</div>
												))}
											</div>
										)}

										<div className="MarketplaceProviderDetails-Address">
											<Location className="MarketplaceProviderDetails-Icon"/>
											<div>{data.state} {data.zipCode}</div>
										</div>

										{data?.shouldReceiveNonNetworkReferrals && (
											<div className="d-flex flex-row flex-wrap">
												<Button
													color="success"
													className="margin-bottom-20"
													onClick={() => toggleInquiryEditorOpen(true)}
												>
													Send Inquiry
												</Button>
											</div>
										)}

										{isNotEmpty(data.servicesSummaryDescription) && (
											<div
												className="MarketplaceProviderDetails-ServicesSummaryDescription"
											>
												<ShowMore
													lines={4}
													more="Read more"
													less="Read less"
													anchorClass="ShowMoreBtn">
													{data.servicesSummaryDescription}
												</ShowMore>
											</div>
										)}

										{isNotEmpty(data.pictures) && (
											<div className="MarketplaceProviderDetails-Section margin-bottom-24">
												<div className="MarketplaceProviderDetails-SectionTitle margin-bottom-20">
													Photos
												</div>
												<Carousel containerClassName="MarketplaceProviderDetails-CommunityPhotosContainer">
													{map(data.pictures, o => (
														<Carousel.Slide>
															<Picture
																hasViewer
																mimeType={o.mimeType}
																className="MarketplaceProviderDetails-CommunityPhoto"
																path={`/open-marketplace/service-providers/${providerId}/pictures/${o.id}`}
															/>
														</Carousel.Slide>
													))}
												</Carousel>
											</div>
										)}

										{isNotEmpty(data.numberOfBeds) && (
											<div className="MarketplaceProviderDetails-NumberOfBeds">
												<div className="MarketplaceProviderDetails-NumberOfBedsTitle">
													<BedPulse className="margin-right-10"/># of Units
												</div>
												<div
													className="MarketplaceProviderDetails-NumberOfVacantBedsTitle">
													<Bed className="margin-right-10"/># of Open Units
												</div>
												<div className="MarketplaceProviderDetails-NumberOfBedsValue">
													{data.numberOfBeds}
												</div>
												<div className="MarketplaceProviderDetails-NumberOfVacantBedsValue">
													{data.numberOfVacantBeds}
												</div>
											</div>
										)}

										<Row>
											{isNotEmpty(data.services) && (
												<Col md={6}>
													<CollapsibleSection
														title="Services"
														isOpenByDefault
														className="MarketplaceProviderDetails-ServicesSection"
														headerClassName="MarketplaceProviderDetails-ServicesSectionHeader"
														titleClassName="MarketplaceProviderDetails-ServicesSectionTitle"
														renderHeaderIcon={({ isOpen, className }) => (
															isOpen ? (
																<ChevronTop className={cn(className, 'MarketplaceProviderDetails-ServicesSectionIcon')}/>
															) : (
																<ChevronBottom className={cn(className, 'MarketplaceProviderDetails-ServicesSectionIcon')}/>
															)
														)}
													>
														{map(data.services, o => (
															<div
																key={o.id}
																className="MarketplaceProviderDetails-Service"
															>
																<div className="margin-right-8">
																	<Checkmark/>
																</div>
																<div>
																	{o.title}
																</div>
															</div>
														))}
													</CollapsibleSection>
												</Col>
											)}
											{isNotEmpty(data.languages) && (
												<Col md={6}>
													<CollapsibleSection
														title="Languages"
														className="MarketplaceProviderDetails-LanguagesSection"
														headerClassName="MarketplaceProviderDetails-LanguagesSectionHeader"
														titleClassName="MarketplaceProviderDetails-LanguagesSectionTitle"
														renderHeaderIcon={({ isOpen, className }) => (
															isOpen ? (
																<ChevronTop className={cn(className, 'MarketplaceProviderDetails-LanguagesSectionIcon')}/>
															) : (
																<ChevronBottom className={cn(className, 'MarketplaceProviderDetails-LanguagesSectionIcon')}/>
															)
														)}
													>
														{map(data.languages, o => (
															<div
																key={o.id}
																className="MarketplaceProviderDetails-Language"
															>
																<div className="margin-right-8">
																	<Checkmark/>
																</div>
																<div>
																	{o.title}
																</div>
															</div>
														))}
													</CollapsibleSection>
												</Col>
											)}
										</Row>
									</div>
								</div>
							)
						}}
					</DataLoadable>


					{error && (
						<ErrorViewer
							isOpen
							error={error}
							onClose={() => setError(null)}
						/>
					)}
				</div>

				<InquiryEditor
					communityId={data?.id}
					serviceCategories={data?.serviceCategories}
					isOpen={isInquiryEditorOpen}
					onClose={onCloseInquiryEditor}
					onSaveSuccess={onSaveInquirySuccess}
				/>

				<Footer hasLogo theme="gray">
					<div className="MarketplaceProviderDetails-OrganizationPhone">
						Call (844) 666-3038
					</div>
				</Footer>
			</div>
		</DocumentTitle>
	)
}

MarketplaceProviderDetails.propTypes = {
	clientId: PTypes.number,
	communityId: PTypes.number,
	communityName: PTypes.string,
	onBack: PTypes.func,
	onChoose: PTypes.func
}

MarketplaceProviderDetails.defaultProps = {
	onBack: noop,
	onChoose: noop
}

export default memo(MarketplaceProviderDetails)