import React from 'react'

import {
	any,
	map,
	chain,
	where,
	filter,
	findWhere
} from 'underscore'

import { rest } from 'msw'
import { setupServer } from 'msw/node'
import { Provider } from 'react-redux'
import { createBrowserHistory } from 'history'
import { waitFor, render } from 'lib/test-utils'

import Response from 'lib/mock/server/Response'

import {
	User,
	State,
	Service,
	Community,
	Organization,
	ServiceCategory,
	MarketplaceLanguage,
	OrganizationDetails
} from 'lib/mock/db/DB'

import configureStore from 'redux/configureStore'

import {
	TestRunner
} from 'lib/test/utils/TestUtils'

import {
	FieldTestFactory
} from 'lib/test/utils/FormTestUtils'

import OrganizationForm from './OrganizationForm'

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
		organizationId = 3,
		userOrganizationId = 3
	}
) {
	const {
		store, ...config
	} = render(
		<OrganizationForm
			testConfig={{ tab }}
			organizationId={organizationId}
		/>
	)

	const user = findWhere(User, { organizationId: userOrganizationId })
	store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

	return { store, ...config }
}

describe('<OrganizationForm>:', () => {
	beforeAll(() => {
		server.listen()
	})

	describe('Legal Info Section:', () => {
		function testFieldToBeVisible(title, name) {
			it(`"${title}" field is visible`, async () => {
				let { getByTestId } = render(<OrganizationForm/>)
				const node = getByTestId(`${name}_field`)
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})
		}

		function testFieldToBeRequired(title, name) {
			it(`"${title}" field is required`, async () => {
				let { getByTestId } = render(<OrganizationForm/>)
				const node = getByTestId(`${name}_field-label`)
				expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
			})
		}

		describe('All fields are visible', function () {
			const factory = new FieldTestFactory(() => <OrganizationForm/>)

			function FieldToBeVisibleTest(title, name) {
				return factory.create(name, title).expectToBeVisible()
			}

			const runner = new TestRunner([
				FieldToBeVisibleTest('Organization Name', 'name'),
				FieldToBeVisibleTest('Organization OID', 'oid'),
				FieldToBeVisibleTest('Company ID', 'companyId'),
				FieldToBeVisibleTest('Email', 'email'),
				FieldToBeVisibleTest('Phone', 'phone'),
				FieldToBeVisibleTest('Street', 'street'),
				FieldToBeVisibleTest('City', 'city'),
				FieldToBeVisibleTest('State', 'stateId'),
				FieldToBeVisibleTest('Zip Code', 'zipCode'),
				FieldToBeVisibleTest('Logo', 'logo')
			])

			runner.run()
		})

		describe('Fields marked as required', function () {
			testFieldToBeRequired('Organization Name', 'name')
			testFieldToBeRequired('Organization OID', 'oid')
			testFieldToBeRequired('Company ID', 'companyId')
			testFieldToBeRequired('Email', 'email')
			testFieldToBeRequired('Phone', 'phone')
			testFieldToBeRequired('Street', 'street')
			testFieldToBeRequired('City', 'city')
			testFieldToBeRequired('State', 'stateId')
			testFieldToBeRequired('Zip Code', 'zipCode')
		})

		describe('Add new Organization. Default Field values:', function () {
			function testTextFieldToHaveValue(title, name, value) {
				it(`"${title}" field initialized correctly`, async () => {
					let { getByTestId } = render(<OrganizationForm/>)
					const node = getByTestId(`${name}_field-input`)
					expect(node).toHaveValue(value)
				})
			}

			function testSelectFieldToHaveValue(title, name, value) {
				it(`"${title}" field initialized correctly`, async () => {
					let { getByTestId } = render(<OrganizationForm/>)
					const node = getByTestId(`${name}_selected-text`)
					expect(node).toHaveTextContent(value)
				})
			}

			testTextFieldToHaveValue('Organization Name', 'name', '')
			testTextFieldToHaveValue('Organization OID', 'oid', '')
			testTextFieldToHaveValue('Company ID', 'companyId', '')
			testTextFieldToHaveValue('Email', 'email', '')
			testTextFieldToHaveValue('Phone', 'phone', '+1')
			testTextFieldToHaveValue('Street', 'street', '')
			testTextFieldToHaveValue('City', 'city', '')
			testSelectFieldToHaveValue('State', 'stateId', 'Select State')
			testTextFieldToHaveValue('Zip Code', 'zipCode', '')
		})

		describe('Edit Organization. Initialization:', function () {
			const organizationId = 3
			const userOrganizationId = 3

			const organization = findWhere(OrganizationDetails, { id: organizationId })
			const state = findWhere(State, { id: organization.stateId })

			function testTextFieldToHaveValue(title, name, value) {
				it(`"${title}" field initialized correctly`, async () => {
					const { getByTestId } = renderForEditing({
						organizationId, userOrganizationId
					})

					await waitFor(() => {
						let node = getByTestId(`${name}_field-input`)
						expect(node).toHaveValue(value)
					})
				})
			}

			function testSelectFieldToHaveValue(title, name, value) {
				it(`"${title}" field initialized correctly`, async () => {
					const { getByTestId } = renderForEditing({
						organizationId, userOrganizationId
					})

					await waitFor(() => {
						let node = getByTestId(`${name}_selected-text`)
						expect(node).toHaveTextContent(value)
					})
				})
			}

			testTextFieldToHaveValue('Organization Name', 'name', organization.name)
			testTextFieldToHaveValue('Organization OID', 'oid', organization.oid)
			testTextFieldToHaveValue('Company ID', 'companyId', organization.companyId)
			testTextFieldToHaveValue('Email', 'email', organization.email)
			testTextFieldToHaveValue('Phone', 'phone', '+' + organization.phone)
			testTextFieldToHaveValue('Street', 'street', organization.street)
			testTextFieldToHaveValue('City', 'city', organization.city)
			testSelectFieldToHaveValue('State', 'stateId', state.label)
			testTextFieldToHaveValue('Zip Code', 'zipCode', organization.zipCode)

			it('Logo', async () => {
				const { getByTestId } = renderForEditing({
					organizationId, userOrganizationId
				})

				await waitFor(() => {
					let node = getByTestId('logo_field-selected-file')
					expect(node).toHaveTextContent(organization.logoName)
				})
			})
		})
	})

	describe('Marketplace Section:', function () {
		function testFieldToBeVisible(title, name) {
			it(`"${title}" field is visible`, async () => {
				let { getByTestId } = render(<OrganizationForm testConfig={{ tab: 1 }}/>)
				const node = getByTestId(`${name}_field`)
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})
		}

		function testFieldToBeRequired(title, name) {
			it(`"${title}" field is required`, async () => {
				let { getByTestId } = render(<OrganizationForm testConfig={{ tab: 1 }}/>)
				const node = getByTestId(`${name}_field-label`)
				expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
			})
		}

		describe('All fields are visible:', function () {
			testFieldToBeVisible('Confirm that organization will be visible in Marketplace', 'marketplace.confirmVisibility')
			testFieldToBeVisible('Allow inbound referral requests from out of network communities', 'allowExternalInboundReferrals')
			testFieldToBeVisible('Services Summary Description', 'marketplace.servicesSummaryDescription')
			testFieldToBeVisible('Categories', 'marketplace.serviceCategoryIds')
			testFieldToBeVisible('Services', 'marketplace.serviceIds')
			testFieldToBeVisible('Languages', 'marketplace.languageIds')
		})

		describe('Fields marked as required:', function () {
			testFieldToBeRequired('Services Summary Description', 'marketplace.servicesSummaryDescription')
			testFieldToBeRequired('Categories', 'marketplace.serviceCategoryIds')
			testFieldToBeRequired('Services', 'marketplace.serviceIds')
		})

		describe('Add new organization. Default field values:', function () {
			function testTextFieldToHaveValue(title, name, value) {
				it(`"${title}" field initialized correctly`, async () => {
					let { getByTestId } = render(<OrganizationForm testConfig={{ tab: 1 }}/>)
					const node = getByTestId(`${name}_field-input`)
					expect(node).toHaveValue(value)
				})
			}

			function testSelectFieldToHaveValue(title, name, value) {
				it(`"${title}" field initialized correctly`, async () => {
					let { getByTestId } = render(<OrganizationForm testConfig={{ tab: 1 }}/>)
					const node = getByTestId(`${name}_selected-text`)
					expect(node).toHaveTextContent(value)
				})
			}

			function testCheckboxFieldToHaveValue(title, name, value) {
				it(`"${title}" field initialized correctly`, async () => {
					let { queryByTestId } = render(<OrganizationForm testConfig={{ tab: 1 }}/>)
					const node = queryByTestId(`${name}_field-check-mark`)
					if (value) {
						expect(node).toBeInTheDocument()
						expect(node).toBeVisible()
					} else expect(node).toBeNull()
				})
			}

			testCheckboxFieldToHaveValue(
				'Confirm that organization will be visible in Marketplace',
				'marketplace.confirmVisibility',
				false
			)

			testCheckboxFieldToHaveValue(
				'Allow inbound referral requests from out of network communities',
				'allowExternalInboundReferrals',
				false
			)

			testTextFieldToHaveValue(
				'Services Summary Description',
				'marketplace.servicesSummaryDescription',
				''
			)

			testSelectFieldToHaveValue('Categories', 'marketplace.serviceCategoryIds', 'Select')
			testSelectFieldToHaveValue('Services', 'marketplace.serviceIds', 'Select')
			testSelectFieldToHaveValue('Languages', 'marketplace.languageIds', 'Select')
		})

		describe('Edit Organization. Initialization:', function () {
			const organizationId = 3
			const userOrganizationId = 3

			const organization = findWhere(OrganizationDetails, { id: organizationId })

			function testTextFieldToHaveValue(title, name, value) {
				it(`"${title}" field initialized correctly`, async () => {
					const { getByTestId } = renderForEditing({
						tab: 1,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						let node = getByTestId(`${name}_field-input`)
						expect(node).toHaveValue(value)
					})
				})
			}

			function testSelectFieldToHaveValue(title, name, value) {
				it(`"${title}" field initialized correctly`, async () => {
					const { getByTestId } = renderForEditing({
						tab: 1,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						let node = getByTestId(`${name}_selected-text`)
						expect(node).toHaveTextContent(value)
					})
				})
			}

			function testCheckboxFieldToHaveValue(title, name, value) {
				it(`"${title}" field initialized correctly`, async () => {
					const { getByTestId } = renderForEditing({
						tab: 1,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						let node = getByTestId(`${name}_field-check-mark`)
						if (value) {
							expect(node).toBeInTheDocument()
							expect(node).toBeVisible()
						} else expect(node).toBeNull()
					})
				})
			}

			testCheckboxFieldToHaveValue(
				'Confirm that organization will be visible in Marketplace',
				'marketplace.confirmVisibility',
				organization.marketplace.confirmVisibility
			)

			testCheckboxFieldToHaveValue(
				'Allow inbound referral requests from out of network communities',
				'allowExternalInboundReferrals',
				organization.allowExternalInboundReferrals
			)

			testTextFieldToHaveValue(
				'Services Summary Description',
				'marketplace.servicesSummaryDescription',
				organization.marketplace.servicesSummaryDescription
			)

			const categoriesValue = chain(ServiceCategory).filter(o => (
				organization.marketplace.serviceCategoryIds.includes(o.id)
			)).map(o => o.title).value().join(', ')

			const servicesValue = chain(Service).filter(o => (
				organization.marketplace.serviceIds.includes(o.id)
			)).map(o => o.title).value().join(', ')

			const languagesValue = chain(MarketplaceLanguage).filter(o => (
				organization.marketplace.languageIds.includes(o.id)
			)).map(o => o.label).value().join(', ')

			testSelectFieldToHaveValue('Categories', 'marketplace.serviceCategoryIds', categoriesValue)
			testSelectFieldToHaveValue('Services', 'marketplace.serviceIds', servicesValue)
			testSelectFieldToHaveValue('Languages', 'marketplace.languageIds', languagesValue)
		})
	})

	describe('Features Section:', function () {
		describe('All fields are visible:', function () {
			function testFieldToBeVisible(title, name) {
				it(`"${title}" field is visible`, async () => {
					let { getByTestId } = render(<OrganizationForm testConfig={{ tab: 2 }}/>)
					const node = getByTestId(`${name}_field`)
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				})
			}

			testFieldToBeVisible('Chat', 'features.isChatEnabled')
			testFieldToBeVisible('Video', 'features.isVideoEnabled')
			testFieldToBeVisible('Comprehensive assessments', 'features.areComprehensiveAssessmentsEnabled')
			testFieldToBeVisible('Paperless Healthcare', 'features.isPaperlessHealthcareEnabled')
			testFieldToBeVisible('Appointments', 'features.areAppointmentsEnabled')
			testFieldToBeVisible('E-sign', 'features.isSignatureEnabled')
		})

		describe('Add new organization. Default field values:', () => {
			function testSwitchFieldToBeChecked(title, name, value) {
				it(`"${title}" field initialized correctly`, async () => {
					let { getByTestId } = render(<OrganizationForm testConfig={{ tab: 2 }}/>)
					const node = getByTestId(`${name}_field-switch`)
					if (value) expect(node).toBeChecked()
					else expect(node).not.toBeChecked()
				})
			}

			testSwitchFieldToBeChecked('Chat', 'features.isChatEnabled', true)
			testSwitchFieldToBeChecked('Video', 'features.isVideoEnabled', true)
			testSwitchFieldToBeChecked('Comprehensive assessments', 'features.areComprehensiveAssessmentsEnabled', false)
			testSwitchFieldToBeChecked('Paperless Healthcare', 'features.isPaperlessHealthcareEnabled', false)
			testSwitchFieldToBeChecked('Appointments', 'features.areAppointmentsEnabled', false)
			testSwitchFieldToBeChecked('E-sign', 'features.isSignatureEnabled', false)
		})

		describe('Edit Organization. Initialization:', function () {
			const organizationId = 3
			const userOrganizationId = 3

			const organization = findWhere(OrganizationDetails, { id: organizationId })

			function testSwitchFieldToBeChecked(title, name, value) {
				it(`"${title}" field initialized correctly`, async () => {
					const { getByTestId } = renderForEditing({
						tab: 2,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						let node = getByTestId(`${name}_field-switch`)
						if (value) expect(node).toBeChecked()
						else expect(node).not.toBeChecked()
					})
				})
			}

			testSwitchFieldToBeChecked(
				'Chat',
				'features.isChatEnabled',
				organization.features.isChatEnabled
			)

			testSwitchFieldToBeChecked(
				'Video',
				'features.isVideoEnabled',
				organization.features.isVideoEnabled
			)

			testSwitchFieldToBeChecked(
				'Comprehensive assessments',
				'features.areComprehensiveAssessmentsEnabled',
				organization.features.areComprehensiveAssessmentsEnabled
			)

			testSwitchFieldToBeChecked(
				'Paperless Healthcare',
				'features.isPaperlessHealthcareEnabled',
				organization.features.isPaperlessHealthcareEnabled
			)

			testSwitchFieldToBeChecked(
				'Appointments',
				'features.areAppointmentsEnabled',
				organization.features.areAppointmentsEnabled
			)

			testSwitchFieldToBeChecked(
				'E-sign',
				'features.isSignatureEnabled',
				organization.features.isSignatureEnabled
			)
		})
	})

	describe('Affiliate Relationship Section', function () {
		const organizationId = 3
		const userOrganizationId = 3

		describe('All fields are visible:', function () {
			it('I want to share information about the events coming to', async () => {
				const { getByTestId } = renderForEditing({
					tab: 3,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('affiliatedRelationships.0.primaryCommunities_field')
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				})
			})

			it('All communities, including those that will be created further', async () => {
				const { getByTestId } = renderForEditing({
					tab: 3,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('affiliatedRelationships.0.areAllPrimaryCommunitiesSelected_field')
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				})
			})

			it('Share with Organization', async () => {
				const { getByTestId } = renderForEditing({
					tab: 3,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('affiliatedRelationships.0.affiliatedOrganization.id_field')
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				})
			})

			it('Share with Community', async () => {
				const { getByTestId } = renderForEditing({
					tab: 3,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('affiliatedRelationships.0.affiliatedCommunities_field')
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				})
			})

			it('All communities, including those that will be created further', async () => {
				const { getByTestId } = renderForEditing({
					tab: 3,
					organizationId,
					userOrganizationId
				})
				await waitFor(() => {
					const node = getByTestId('affiliatedRelationships.0.areAllAffiliatedCommunitiesSelected_field')
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				})
			})
		})

		describe('Fields marked as required:', function () {
			it('I want to share information about the events coming to', async () => {
				const { getByTestId } = renderForEditing({
					tab: 3,
					organizationId,
					userOrganizationId
				})
				await waitFor(() => {
					const node = getByTestId('affiliatedRelationships.0.primaryCommunities_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})
			})

			it('Share with Organization', async () => {
				const { getByTestId } = renderForEditing({
					tab: 3,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('affiliatedRelationships.0.affiliatedOrganization.id_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})
			})

			it('Share with Community', async () => {
				const { getByTestId } = renderForEditing({
					tab: 3,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('affiliatedRelationships.0.affiliatedCommunities_field-label')
					expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
				})
			})
		})

		describe('Edit Organization. Initialization:', function () {
			const organizationId = 3
			const userOrganizationId = 3

			const organization = findWhere(OrganizationDetails, { id: organizationId })

			describe('Relationship 1:', function () {
				it('"I want to share information about the events coming to" field initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						tab: 3,
						organizationId,
						userOrganizationId
					})

					const value = map(
						filter(Community, o => any(organization.affiliatedRelationships[0].primaryCommunities, x => x.id === o.id)),
						o => o.name
					).join(', ')

					await waitFor(() => {
						let node = getByTestId('affiliatedRelationships.0.primaryCommunities_selected-text')
						expect(node).toHaveTextContent(value)
					}, { timeout: 3000 })
				})

				it('"All communities, including those that will be created further" field initialized correctly', async () => {
					const { queryByTestId } = renderForEditing({
						tab: 3,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						let node = queryByTestId('affiliatedRelationships.0.areAllPrimaryCommunitiesSelected_field-check-mark')
						expect(node).toBeNull()
					}, { timeout: 3000 })
				})

				it('"Share with Organization" field initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						tab: 3,
						organizationId,
						userOrganizationId
					})

					const value = map(
						filter(Organization, o => organization.affiliatedRelationships[0].affiliatedOrganization.id === o.id),
						o => o.label
					).join(', ')

					await waitFor(() => {
						let node = getByTestId('affiliatedRelationships.0.affiliatedOrganization.id_selected-text')
						expect(node).toHaveTextContent(value)
					}, { timeout: 3000 })
				})

				it('"Share with Community" field initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						tab: 3,
						organizationId,
						userOrganizationId
					})

					const value = map(
						filter(Community, o => any(organization.affiliatedRelationships[0].affiliatedCommunities, x => x.id === o.id)),
						o => o.name
					).join(', ')

					await waitFor(() => {
						let node = getByTestId('affiliatedRelationships.0.affiliatedCommunities_selected-text')
						expect(node).toHaveTextContent(value)
					}, { timeout: 3000 })
				})

				it('"All communities, including those that will be created further" field initialized correctly', async () => {
					const { queryByTestId } = renderForEditing({
						tab: 3,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						let node = queryByTestId('affiliatedRelationships.0.areAllAffiliatedCommunitiesSelected_field-check-mark')
						expect(node).toBeNull()
					}, { timeout: 3000 })
				})
			})

			describe('Relationship 2:', function () {
				it('"I want to share information about the events coming to" field initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						tab: 3,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						let node = getByTestId('affiliatedRelationships.1.primaryCommunities_selected-text')
						expect(node).toHaveTextContent('Select community')
					}, { timeout: 3000 })
				})

				it('"I want to share information about the events coming to" field is disabled', async () => {
					const { getByTestId } = renderForEditing({
						tab: 3,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						let node = getByTestId('affiliatedRelationships.1.primaryCommunities_field')
						expect(node).toHaveClass('SelectField_disabled')
					}, { timeout: 3000 })
				})

				it('"All communities, including those that will be created further" field initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						tab: 3,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						let node = getByTestId('affiliatedRelationships.1.areAllPrimaryCommunitiesSelected_field-check-mark')
						expect(node).toBeInTheDocument()
						expect(node).toBeVisible()
					}, { timeout: 3000 })
				})

				it('"Share with Organization" field initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						tab: 3,
						organizationId,
						userOrganizationId
					})

					const value = map(
						filter(Organization, o => organization.affiliatedRelationships[1].affiliatedOrganization.id === o.id),
						o => o.label
					).join(', ')

					await waitFor(() => {
						let node = getByTestId('affiliatedRelationships.1.affiliatedOrganization.id_selected-text')
						expect(node).toHaveTextContent(value)
					}, { timeout: 3000 })
				})

				it('"Share with Community" field initialized correctly', async () => {
					const { getByTestId } = renderForEditing({
						tab: 3,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						let node = getByTestId('affiliatedRelationships.1.affiliatedCommunities_selected-text')
						expect(node).toHaveTextContent('Select community')
					}, { timeout: 3000 })
				})

				it('"Share with Community" field is disabled', async () => {
					const { getByTestId } = renderForEditing({
						tab: 3,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						let node = getByTestId('affiliatedRelationships.1.affiliatedCommunities_field')
						expect(node).toHaveClass('SelectField_disabled')
					}, { timeout: 3000 })
				})

				it('"All communities, including those that will be created further" field initialized correctly', async () => {
					const { queryByTestId } = renderForEditing({
						tab: 3,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						let node = queryByTestId('affiliatedRelationships.1.areAllAffiliatedCommunitiesSelected_field-check-mark')
						expect(node).toBeInTheDocument()
						expect(node).toBeVisible()
					}, { timeout: 3000 })
				})
			})
		})
	})

	afterAll(() => {
		server.close()
	})
})