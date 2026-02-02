import React from 'react'

import {
	map,
	where,
	filter,
	findWhere
} from 'underscore'

import { rest } from 'msw'
import { setupServer } from 'msw/node'
import { Provider } from 'react-redux'
import { createBrowserHistory } from 'history'
import { waitFor, render } from 'lib/test-utils'

import {
	CONTACT_STATUSES
} from 'lib/Constants'

import Response from 'lib/mock/server/Response'

import {
	User,
	State,
	Service,
	Community,
	Organization,
	ServiceCategory,
	MarketplaceLanguage,
	CommunityDetails,
	OrganizationDetails
} from 'lib/mock/db/DB'

import configureStore from 'redux/configureStore'

import CommunityForm from './CommunityForm'

const {
	ACTIVE
} = CONTACT_STATUSES

const BASE_URL = 'https://dev.simplyconnect.me/web-portal-mock-backend'

const server = setupServer(
	rest.get(`${BASE_URL}/authorized-directory/organizations`, (req, res, ctx) => {
		return res(ctx.json(Response.success(Organization)))
	}),
	rest.get(`${BASE_URL}/authorized-directory/communities`, (req, res, ctx) => {
		return res(ctx.json(Response.success(
			where(Community, { organizationId: +req.url.searchParams.get('organizationId') })
		)))
	}),
	rest.get(`${BASE_URL}/organizations/:organizationId`, (req, res, ctx) => {
		return res(ctx.json(Response.success(
			findWhere(OrganizationDetails, { id: +req.params.organizationId })
		)))
	}),
	rest.get(`${BASE_URL}/organizations/:organizationId/communities/permissions`, (req, res, ctx) => {
		return res(ctx.json(Response.success({
			"canAdd": true,
			"canEditDocutrack": true,
			"canEditSignatureSetup": false,
			"canEditHieConsentPolicy": true,
			"canEditAllowExternalInboundReferrals": true,
			"canEditConfirmMarketplaceVisibility": true,
			"canEditMarketplaceReferralEmails": true,
			"canEditFeaturedServiceProviders": true
		})))
	}),
	rest.get(`${BASE_URL}/organizations/:organizationId/communities/:communityId`, (req, res, ctx) => {
		return res(ctx.json(Response.success(
			findWhere(CommunityDetails, { id: +req.params.communityId })
		)))
	}),
	rest.get(`${BASE_URL}/docutrack/can-configure`, (req, res, ctx) => {
		return res(ctx.json(Response.success(true)))
	}),
	rest.get(`${BASE_URL}/directory/states`, (req, res, ctx) => {
		return res(ctx.json(Response.success(State)))
	}),
	rest.get(`${BASE_URL}/authorized-directory/service-categories`, (req, res, ctx) => {
		return res(ctx.json(Response.success(ServiceCategory)))
	}),
	rest.get(`${BASE_URL}/authorized-directory/services`, (req, res, ctx) => {
		return res(ctx.json(Response.success(Service)))
	}),
	rest.get(`${BASE_URL}/authorized-directory/marketplace-languages`, (req, res, ctx) => {
		return res(ctx.json(Response.success(MarketplaceLanguage)))
	})
)

function renderForEditing(
	{
		tab = 0,
		communityId,
		organizationId = 3,
		userOrganizationId = 3
	}
) {
	const {
		store, ...config
	} = render(
		<CommunityForm
			defaultActiveTab={tab}
			communityId={communityId}
			organizationId={organizationId}
		/>
	)

	const user = findWhere(User, { organizationId: userOrganizationId })
	store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

	return { store, ...config }
}

describe('<CommunityForm>:', () => {
	beforeAll(() => {
		server.listen()
	})

	describe('Legal Info Section:', () => {
		describe('All fields are visible:', function () {
			describe('General Data:', function () {
				it('Community Name', async () => {
					let { getByTestId } = render(<CommunityForm/>)
					const node = getByTestId('name_field')
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				})

				it('Community OID', async () => {
					let { getByTestId } = render(<CommunityForm/>)
					const node = getByTestId('oid_field')
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				})

				it('License #', async () => {
					let { getByTestId } = render(<CommunityForm/>)
					const node = getByTestId('licenseNumber_field')
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				})

				it('# of units', async () => {
					let { getByTestId } = render(<CommunityForm/>)
					const node = getByTestId('numberOfBeds_field')
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				})

				it('# of open units', async () => {
					let { getByTestId } = render(<CommunityForm/>)
					const node = getByTestId('numberOfVacantBeds_field')
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				})
			})

			describe('Contact Info:', function () {
				it('Email', async () => {
					let { getByTestId } = render(<CommunityForm/>)
					const node = getByTestId('email_field')
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				})

				it('Phone', async () => {
					let { getByTestId } = render(<CommunityForm/>)
					const node = getByTestId('phone_field')
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				})

				it('Street', async () => {
					let { getByTestId } = render(<CommunityForm/>)
					const node = getByTestId('street_field')
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				})

				it('City', async () => {
					let { getByTestId } = render(<CommunityForm/>)
					const node = getByTestId('city_field')
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				})

				it('State', async () => {
					let { getByTestId } = render(<CommunityForm/>)
					const node = getByTestId('stateId_field')
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				})

				it('Zip Code', async () => {
					let { getByTestId } = render(<CommunityForm/>)
					const node = getByTestId('zipCode_field')
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				})

				it('Website Url', async () => {
					let { getByTestId } = render(<CommunityForm/>)
					const node = getByTestId('websiteUrl_field')
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				})
			})

			describe('Photos:', function () {
				it('Logo', async () => {
					let { getByTestId } = render(<CommunityForm/>)
					const node = getByTestId('zipCode_field')
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				})
			})

			describe('DocuTrack Setup:', function () {
				const organizationId = 3
				const userOrganizationId = 3

				it('Add BUC Button', async () => {
					const { getByTestId } = renderForEditing({
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						const node = getByTestId('add-buc-btn')
						expect(node).toBeInTheDocument()
						expect(node).toBeVisible()
					})
				})

				it('Enable Integration', async () => {
					const { getByTestId } = renderForEditing({
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						const node = getByTestId('docutrackPharmacyConfig.isIntegrationEnabled_field')
						expect(node).toBeInTheDocument()
						expect(node).toBeVisible()
					})
				})

				it('Client Type', async () => {
					const { getByTestId } = renderForEditing({
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						const node = getByTestId('docutrackPharmacyConfig.clientType_field')
						expect(node).toBeInTheDocument()
						expect(node).toBeVisible()
					})
				})

				it('Server domain', async () => {
					const { getByTestId } = renderForEditing({
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						const node = getByTestId('docutrackPharmacyConfig.serverDomain_field')
						expect(node).toBeInTheDocument()
						expect(node).toBeVisible()
					})
				})

				it('Get certificate', async () => {
					const { getByTestId } = renderForEditing({
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						const node = getByTestId('get-certificate-btn')
						expect(node).toBeInTheDocument()
						expect(node).toBeVisible()
					})
				})

				it('Business Unit Code', async () => {
					const { getByTestId } = renderForEditing({
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						const node = getByTestId('docutrackPharmacyConfig.businessUnitCodes.0_field')
						expect(node).toBeInTheDocument()
						expect(node).toBeVisible()
					})
				})
			})

			describe('E-signature Setup:', function () {
				const organizationId = 3
				const userOrganizationId = 3

				it('Enable Security PIN', async () => {
					const { getByTestId } = renderForEditing({
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						const node = getByTestId('signatureConfig.isPinEnabled_field')
						expect(node).toBeInTheDocument()
						expect(node).toBeVisible()
					})
				})
			})
		})

		describe('Fields marked as required:', function () {
			describe('General Data:', function () {
				it('Community Name', async () => {
					let { getByTestId } = render(<CommunityForm/>)
					const node = getByTestId('name_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})

				it('Community OID', async () => {
					let { getByTestId } = render(<CommunityForm/>)
					const node = getByTestId('oid_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})
			})

			describe('Contact Info:', function () {
				it('Email', async () => {
					let { getByTestId } = render(<CommunityForm/>)
					const node = getByTestId('email_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})

				it('Phone', async () => {
					let { getByTestId } = render(<CommunityForm/>)
					const node = getByTestId('phone_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})

				it('Street', async () => {
					let { getByTestId } = render(<CommunityForm/>)
					const node = getByTestId('street_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})

				it('City', async () => {
					let { getByTestId } = render(<CommunityForm/>)
					const node = getByTestId('city_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})

				it('State', async () => {
					let { getByTestId } = render(<CommunityForm/>)
					const node = getByTestId('stateId_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})

				it('Zip Code', async () => {
					let { getByTestId } = render(<CommunityForm/>)
					const node = getByTestId('zipCode_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})
			})

			describe('DocuTrack Setup:', function () {
				const organizationId = 3
				const userOrganizationId = 3

				it('Client Type', async () => {
					const { getByTestId } = renderForEditing({
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						const node = getByTestId('docutrackPharmacyConfig.clientType_field-label')
						expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
					})
				})

				it('Server domain', async () => {
					const { getByTestId } = renderForEditing({
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						const node = getByTestId('docutrackPharmacyConfig.serverDomain_field-label')
						expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
					})
				})
			})
		})

		describe('Add new Community. Default Field values:', function () {
			describe('General Data:', function () {
				it('"Community Name" field initialized correctly', async () => {
					let { getByTestId } = render(<CommunityForm/>)
					const node = getByTestId('name_field-input')
					expect(node).toHaveValue('')
				})

				it('"Community OID" field initialized correctly', async () => {
					let { getByTestId } = render(<CommunityForm/>)
					const node = getByTestId('oid_field-input')
					expect(node).toHaveValue('')
				})

				it('"License #" field initialized correctly', async () => {
					let { getByTestId } = render(<CommunityForm/>)
					const node = getByTestId('licenseNumber_field-input')
					expect(node).toHaveValue('')
				})

				it('"# of units" field initialized correctly', async () => {
					let { getByTestId } = render(<CommunityForm/>)
					const node = getByTestId('numberOfBeds_field-input')
					expect(node).toHaveValue('')
				})

				it('"# of open units" field initialized correctly', async () => {
					let { getByTestId } = render(<CommunityForm/>)
					const node = getByTestId('numberOfVacantBeds_field-input')
					expect(node).toHaveValue('')
				})
			})

			describe('Contact Info:', function () {
				it('"Email" field initialized correctly', async () => {
					let { getByTestId } = render(<CommunityForm/>)
					const node = getByTestId('email_field-input')
					expect(node).toHaveValue('')
				})

				it('"Phone" field initialized correctly', async () => {
					let { getByTestId } = render(<CommunityForm/>)
					const node = getByTestId('phone_field-input')
					expect(node).toHaveValue('+1')
				})

				it('"Street" field initialized correctly', async () => {
					let { getByTestId } = render(<CommunityForm/>)
					const node = getByTestId('street_field-input')
					expect(node).toHaveValue('')
				})

				it('"City" field initialized correctly', async () => {
					let { getByTestId } = render(<CommunityForm/>)
					const node = getByTestId('city_field-input')
					expect(node).toHaveValue('')
				})

				it('"State" field initialized correctly', async () => {
					let { getByTestId } = render(<CommunityForm/>)
					const node = getByTestId('stateId_selected-text')
					expect(node).toHaveTextContent('Select State')
				})

				it('"Zip Code" field initialized correctly', async () => {
					let { getByTestId } = render(<CommunityForm/>)
					const node = getByTestId('zipCode_field-input')
					expect(node).toHaveValue('')
				})

				it('"Website Url" field initialized correctly', async () => {
					let { getByTestId } = render(<CommunityForm/>)
					const node = getByTestId('websiteUrl_field-input')
					expect(node).toHaveValue('')
				})
			})

			describe('DocuTrack Setup:', function () {
				const organizationId = 3
				const userOrganizationId = 3

				it('"Add BUC Button" button initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						const node = getByTestId('add-buc-btn')
						expect(node).toHaveClass('disabled')
					})
				})

				it('"Enable Integration" field initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						const node = getByTestId('docutrackPharmacyConfig.isIntegrationEnabled_field-switch')
						expect(node).not.toBeChecked()
					})
				})

				it('"Client Type" field initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						const node = getByTestId('docutrackPharmacyConfig.clientType_field-input')
						expect(node).toHaveValue('')
					})
				})

				it('"Server domain" field initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						const node = getByTestId('docutrackPharmacyConfig.serverDomain_field-input')
						expect(node).toHaveValue('')
					})
				})

				it('"Get certificate" field initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						const node = getByTestId('get-certificate-btn')
						expect(node).toHaveClass('disabled')
					})
				})

				it('"Business Unit Code" field initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						const node = getByTestId('docutrackPharmacyConfig.businessUnitCodes.0_field-input')
						expect(node).toHaveValue('')
					})
				})
			})

			describe('E-signature Setup:', function () {
				const organizationId = 3
				const userOrganizationId = 3

				it('"Enable Security PIN" field initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						const node = getByTestId('signatureConfig.isPinEnabled_field-switch')
						expect(node).toBeChecked()
					})
				})
			})
		})

		describe('Edit Community. Initialization:', function () {
			const organizationId = 3
			const communityId = 83612
			const userOrganizationId = 3

			const community = findWhere(CommunityDetails, { id: communityId })

			describe('General Data:', function () {
				it('"Community Name" field initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						communityId,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						let node = getByTestId('name_field-input')
						expect(node).toHaveValue(community.name)
					})
				})

				it('"Community OID" field initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						communityId,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						let node = getByTestId('oid_field-input')
						expect(node).toHaveValue(community.oid)
					})
				})

				it('"License #" field initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						communityId,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						let node = getByTestId('licenseNumber_field-input')
						expect(node).toHaveValue(community.licenseNumber)
					})
				})

				it('"# of units" field initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						communityId,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						let node = getByTestId('numberOfBeds_field-input')
						expect(node).toHaveValue(community.numberOfBeds)
					})
				})

				it('"# of open units" field initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						communityId,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						let node = getByTestId('numberOfVacantBeds_field-input')
						expect(node).toHaveValue(community.numberOfVacantBeds)
					})
				})
			})

			describe('Contact Info:', function () {
				it('"Email" field initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						communityId, organizationId, userOrganizationId
					})

					await waitFor(() => {
						let node = getByTestId('email_field-input')
						expect(node).toHaveValue(community.email)
					})
				})

				it('"Phone" field initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						communityId, organizationId, userOrganizationId
					})

					await waitFor(() => {
						let node = getByTestId('phone_field-input')
						expect(node).toHaveValue('+' + community.phone)
					})
				})

				it('"Street" field initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						communityId, organizationId, userOrganizationId
					})

					await waitFor(() => {
						let node = getByTestId('street_field-input')
						expect(node).toHaveValue(community.street)
					})
				})

				it('"City" field initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						communityId, organizationId, userOrganizationId
					})

					await waitFor(() => {
						let node = getByTestId('city_field-input')
						expect(node).toHaveValue(community.city)
					})
				})

				it('"State" field initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						communityId, organizationId, userOrganizationId
					})

					await waitFor(() => {
						let node = getByTestId('stateId_selected-text')
						expect(node).toHaveTextContent('Georgia (GA)')
					})
				})

				it('"Zip Code" field initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						communityId, organizationId, userOrganizationId
					})

					await waitFor(() => {
						let node = getByTestId('zipCode_field-input')
						expect(node).toHaveValue(community.zipCode)
					})
				})

				it('"Website Url" field initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						communityId, organizationId, userOrganizationId
					})

					await waitFor(() => {
						let node = getByTestId('websiteUrl_field-input')
						expect(node).toHaveValue(community.websiteUrl)
					})
				})
			})

			describe('Photos', function () {
				it('"Community Logo" field initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						communityId, organizationId, userOrganizationId
					})

					await waitFor(() => {
						let node = getByTestId('logo_field-selected-file')
						expect(node).toHaveTextContent(community.logoName)
					})
				})
			})

			describe('DocuTrack Setup:', function () {

				it('"Add BUC Button" field initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						communityId,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						const node = getByTestId('add-buc-btn')
						expect(node).not.toHaveClass('disabled')
					})
				})

				it('"Enable Integration" field initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						communityId,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						const node = getByTestId('docutrackPharmacyConfig.isIntegrationEnabled_field-switch')
						expect(node).toBeChecked()
					})
				})

				it('"Client Type" field initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						communityId,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						const node = getByTestId('docutrackPharmacyConfig.clientType_field-input')
						expect(node).toHaveValue(community.docutrackPharmacyConfig.clientType)
					})
				})

				it('"Server domain" field initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						communityId,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						const node = getByTestId('docutrackPharmacyConfig.serverDomain_field-input')
						expect(node).toHaveValue(community.docutrackPharmacyConfig.serverDomain)
					})
				})

				it('"Get Certificate" Button initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						communityId,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						const node = getByTestId('get-certificate-btn')
						expect(node).not.toHaveClass('disabled')
					})
				})

				it('"Business Unit Code" field initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						communityId,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						const node = getByTestId('docutrackPharmacyConfig.businessUnitCodes.0_field-input')
						expect(node).toHaveValue(community.docutrackPharmacyConfig.businessUnitCodes[0])
					})
				})
			})

			describe('E-signature Setup:', function () {
				it('"Enable Security PIN" field initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						communityId, organizationId, userOrganizationId
					})

					await waitFor(() => {
						const node = getByTestId('signatureConfig.isPinEnabled_field-switch')
						expect(node).toBeChecked()
					})
				})
			})
		})
	})

	describe('Marketplace Section:', function () {

		describe('All fields are visible:', function () {
			it('Confirm that community will be visible in Marketplace', async () => {
				let { getByTestId } = render(<CommunityForm defaultActiveTab={1}/>)
				const node = getByTestId('marketplace.confirmVisibility_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Allow inbound referral requests from out of network communities', async () => {
				let { getByTestId } = render(<CommunityForm defaultActiveTab={1}/>)
				const node = getByTestId('marketplace.allowExternalInboundReferrals_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Services Summary Description', async () => {
				let { getByTestId } = render(<CommunityForm defaultActiveTab={1}/>)
				const node = getByTestId('marketplace.servicesSummaryDescription_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Categories', async () => {
				let { getByTestId } = render(<CommunityForm defaultActiveTab={1}/>)
				const node = getByTestId('marketplace.serviceCategoryIds_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Services', async () => {
				let { getByTestId } = render(<CommunityForm defaultActiveTab={1}/>)
				const node = getByTestId('marketplace.serviceIds_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Languages', async () => {
				let { getByTestId } = render(<CommunityForm defaultActiveTab={1}/>)
				const node = getByTestId('marketplace.languageIds_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})
		})

		describe('Fields marked as required:', function () {
			it('Services Summary Description', async () => {
				let { getByTestId } = render(<CommunityForm defaultActiveTab={1}/>)
				const node = getByTestId('marketplace.servicesSummaryDescription_field-label')
				expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
			})

			it('Categories', async () => {
				let { getByTestId } = render(<CommunityForm defaultActiveTab={1}/>)
				const node = getByTestId('marketplace.serviceCategoryIds_field-label')
				expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
			})

			it('Services', async () => {
				let { getByTestId } = render(<CommunityForm defaultActiveTab={1}/>)
				const node = getByTestId('marketplace.serviceIds_field-label')
				expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
			})

			describe('Referrals:', function () {
				const organizationId = 3
				const userOrganizationId = 3

				it('Email', async () => {
					const { getByTestId } = renderForEditing({
						tab: 1,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						let node = getByTestId('marketplace.referralEmails.0.value_field-label')
						expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
					})
				})
			})
		})

		describe('Add new community. Default field values:', function () {
			const organizationId = 3
			const userOrganizationId = 3

			const organization = findWhere(OrganizationDetails, { id: organizationId })

			it('"Confirm that organization will be visible in Marketplace" field initialized correctly', async () => {
				const { getByTestId } = renderForEditing({
					tab: 1,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					let node = getByTestId('marketplace.confirmVisibility_field-check-mark')

					if (organization.marketplace.confirmVisibility) {
						expect(node).toBeInTheDocument()
						expect(node).toBeVisible()
					} else {
						expect(node).not.toBeInTheDocument()
						expect(node).not.toBeVisible()
					}
				})
			})

			it('"Allow inbound referral requests from out of network communities" field initialized correctly', async () => {
				const { getByTestId } = renderForEditing({
					tab: 1,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					let node = getByTestId('marketplace.allowExternalInboundReferrals_field-check-mark')

					if (organization.allowExternalInboundReferrals) {
						expect(node).toBeInTheDocument()
						expect(node).toBeVisible()
					} else {
						expect(node).not.toBeInTheDocument()
						expect(node).not.toBeVisible()
					}
				})
			})

			it('"Services Summary Description" field initialized correctly', async () => {
				const { getByTestId } = renderForEditing({
					tab: 1,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					let node = getByTestId('marketplace.servicesSummaryDescription_field-input')
					expect(node).toHaveValue(organization.marketplace.servicesSummaryDescription)
				})
			})

			it('"Categories" field initialized correctly', async () => {
				const { getByTestId } = renderForEditing({
					tab: 1,
					organizationId,
					userOrganizationId
				})

				const value = map(
					filter(ServiceCategory, o => organization.marketplace.serviceCategoryIds.includes(o.id)),
					o => o.title
				).join(', ')

				await waitFor(() => {
					const node = getByTestId('marketplace.serviceCategoryIds_selected-text')
					expect(node).toHaveTextContent(value)
				})
			})

			it('"Services" field initialized correctly', async () => {
				const { getByTestId } = renderForEditing({
					tab: 1,
					organizationId,
					userOrganizationId
				})

				const value = map(
					filter(Service, o => organization.marketplace.serviceIds.includes(o.id)),
					o => o.title
				).join(', ')

				await waitFor(() => {
					const node = getByTestId('marketplace.serviceIds_selected-text')
					expect(node).toHaveTextContent(value)
				})
			})

			it('"Languages" field initialized correctly', async () => {
				const { getByTestId } = renderForEditing({
					tab: 1,
					organizationId,
					userOrganizationId
				})

				const value = map(
					filter(MarketplaceLanguage, o => organization.marketplace.languageIds.includes(o.id)),
					o => o.label
				).join(', ')

				await waitFor(() => {
					let node = getByTestId('marketplace.languageIds_selected-text')
					expect(node).toHaveTextContent(value)
				})
			})
		})

		describe('Edit Community. Initialization:', function () {
			const organizationId = 3
			const communityId = 83612
			const userOrganizationId = 3

			const community = findWhere(CommunityDetails, { id: communityId })

			it('"Confirm that organization will be visible in Marketplace" field initialized correctly', async () => {
				const { getByTestId } = renderForEditing({
					tab: 1,
					communityId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					let node = getByTestId('marketplace.confirmVisibility_field-check-mark')

					if (community.marketplace.confirmVisibility) {
						expect(node).toBeInTheDocument()
						expect(node).toBeVisible()
					} else {
						expect(node).not.toBeInTheDocument()
						expect(node).not.toBeVisible()
					}
				})
			})

			it('"Allow inbound referral requests from out of network communities" field initialized correctly', async () => {
				const { getByTestId } = renderForEditing({
					tab: 1,
					communityId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					let node = getByTestId('marketplace.allowExternalInboundReferrals_field-check-mark')

					if (community.allowExternalInboundReferrals) {
						expect(node).toBeInTheDocument()
						expect(node).toBeVisible()
					} else {
						expect(node).not.toBeInTheDocument()
						expect(node).not.toBeVisible()
					}
				})
			})

			it('"Services Summary Description" field initialized correctly', async () => {
				const { getByTestId } = renderForEditing({
					tab: 1,
					communityId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					let node = getByTestId('marketplace.servicesSummaryDescription_field-input')
					expect(node).toHaveValue(community.marketplace.servicesSummaryDescription)
				})
			})

			it('"Categories" field initialized correctly', async () => {
				const { getByTestId } = renderForEditing({
					tab: 1,
					communityId,
					organizationId,
					userOrganizationId
				})

				const value = map(
					filter(ServiceCategory, o => community.marketplace.serviceCategoryIds.includes(o.id)),
					o => o.title
				).join(', ')

				await waitFor(() => {
					const node = getByTestId('marketplace.serviceCategoryIds_selected-text')
					expect(node).toHaveTextContent(value)
				})
			})

			it('"Services" field initialized correctly', async () => {
				const { getByTestId } = renderForEditing({
					tab: 1,
					communityId,
					organizationId,
					userOrganizationId
				})

				const value = map(
					filter(Service, o => community.marketplace.serviceIds.includes(o.id)),
					o => o.title
				).join(', ')

				await waitFor(() => {
					let node = getByTestId('marketplace.serviceIds_selected-text')
					expect(node).toHaveTextContent(value)
				})
			})

			it('"Languages" field initialized correctly', async () => {
				const { getByTestId } = renderForEditing({
					tab: 1,
					communityId,
					organizationId,
					userOrganizationId
				})

				const value = map(
					filter(MarketplaceLanguage, o => community.marketplace.languageIds.includes(o.id)),
					o => o.label
				).join(', ')

				await waitFor(() => {
					let node = getByTestId('marketplace.languageIds_selected-text')
					expect(node).toHaveTextContent(value)
				})
			})

			describe('Referrals:', function () {
				it('Email', async () => {
					const { getByTestId } = renderForEditing({
						tab: 1,
						communityId,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						for (let i = 0; i < community.marketplace.referralEmails.length; i++) {
							let node = getByTestId(`marketplace.referralEmails.${i}.value_field-input`)
							expect(node).toHaveValue(community.marketplace.referralEmails[i])
						}
					})
				})
			})
		})
	})

	afterAll(() => {
		server.close()
	})
})