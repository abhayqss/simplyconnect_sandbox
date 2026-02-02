import React from 'react'

import {
	where,
	findWhere
} from 'underscore'

import { rest } from 'msw'
import { setupServer } from 'msw/node'
import { waitFor, render } from 'lib/test-utils'

import Response from 'lib/mock/server/Response'

import {
	User,
	State,
	Community,
	SystemRole,
	Organization,
	ClientDetails,
	ContactDetails,
	CommunityDetails,
	UnassociatedClient,
	OrganizationDetails
} from 'lib/mock/db/DB'

import ContactForm from './ContactForm'

const BASE_URL = 'https://dev.simplyconnect.me/web-portal-mock-backend'

const server = setupServer(	
	rest.get(`${BASE_URL}/clients/unassociated`, (req, res, ctx) => {
		return res(ctx.json(Response.success(UnassociatedClient)))
	}),
	rest.get(`${BASE_URL}/clients/:clientId`, (req, res, ctx) => {
		return res(ctx.json(Response.success(
			findWhere(ClientDetails, { id: +req.params.clientId })
		)))
	}),
	rest.get(`${BASE_URL}/contacts/:contactId`, (req, res, ctx) => {
		return res(ctx.json(Response.success(
			findWhere(ContactDetails, { id: +req.params.contactId })
		)))
	}),
	rest.get(`${BASE_URL}/authorized-directory/organizations`, (req, res, ctx) => {
		return res(ctx.json(Response.success(Organization)))
	}),
	rest.get(`${BASE_URL}/authorized-directory/communities`, (req, res, ctx) => {
		return res(ctx.json(Response.success(
			where(Community, { organizationId: +req.url.searchParams.get('organizationId') })
		)))
	}),
	rest.get(`${BASE_URL}/directory/states`, (req, res, ctx) => {
		return res(ctx.json(Response.success(State)))
	}),
	rest.get(`${BASE_URL}/authorized-directory/editable-system-roles`, (req, res, ctx) => {
		return res(ctx.json(Response.success(SystemRole)))
	}),
	rest.get(`${BASE_URL}/organizations/:organizationId/communities/:communityId`, (req, res, ctx) => {
		return res(ctx.json(Response.success(
			findWhere(Community, {
				organizationId: +req.params.organizationId,
				id: +req.params.communityId
			})
		)))
	})
)

function renderAndConfigure(
	{
		clientId,
		contactId,
		isExpiredContact,
		isPendingContact,
		canEditRole = true,
		organizationId = 3,
		userOrganizationId = 3
	}
) {
	const {
		store, ...config
	} = render(
		<ContactForm
			clientId={clientId}
			contactId={contactId}
			canEditRole={canEditRole}
			organizationId={organizationId}
			isExpiredContact={isExpiredContact}
			isPendingContact={isPendingContact}
		/>
	)

	const user = findWhere(User, { organizationId: userOrganizationId })
	store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

	return { store, ...config }
}

describe('<ContactForm>:', function () {
	beforeAll(() => {
		server.listen()
	})

	describe('All fields are visible:', function () {
		const organizationId = 3

		describe('General Data:', function () {
			it('First Name', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('firstName_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Last Name', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('lastName_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Login', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('login_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('System Role (canEditRole=true)', async () => {
				let { getByTestId } = render(
					<ContactForm
						canEditRole
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('systemRoleId_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Professionals', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('professionals_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Organization', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('organizationId_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Community', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('communityId_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Profile photo', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('avatar_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})
		})

		describe('Contact Info:', function () {
			it('Use community address', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('isCommunityAddressUsed_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Street', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('street_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('City', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('city_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('State', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('stateId_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Zip Code', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('zip_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Mobile Phone', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('mobilePhone_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Phone', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('phone_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Fax', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('fax_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('Secure Email', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('secureMail_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})
		})

		describe('Settings:', function () {
			it('Enable contact', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('enableContact_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('QA', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('qaIncidentReports_field')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})
		})
	})

	describe('Fields marked as required:', function () {
		const organizationId = 3

		describe('General Data:', function () {
			it('First Name', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('firstName_field-label')
				expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
			})

			it('Last Name', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('lastName_field-label')
				expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
			})

			it('Login', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('login_field-label')
				expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
			})

			it('System Role (canEditRole=true)', async () => {
				let { getByTestId } = render(
					<ContactForm
						canEditRole
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('systemRoleId_field-label')
				expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
			})

			it('Organization', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('organizationId_field-label')
				expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
			})

			it('Community', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('communityId_field-label')
				expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
			})
		})

		describe('Contact Info:', function () {
			it('Street', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('street_field-label')
				expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
			})

			it('City', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('city_field-label')
				expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
			})

			it('State', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('stateId_field-label')
				expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
			})

			it('Zip Code', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('zip_field-label')
				expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
			})

			it('Mobile Phone', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('mobilePhone_field-label')
				expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
			})
		})
	})

	describe('Add new Contact. Default Field values:', function () {
		const organizationId = 3
		const userOrganizationId = 3

		const organization = findWhere(OrganizationDetails, { id: organizationId })

		describe('General Data:', function () {
			it('"First Name" field initialized correctly', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('firstName_field-input')
				expect(node).toHaveValue('')
			})

			it('"Last Name" field initialized correctly', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('lastName_field-input')
				expect(node).toHaveValue('')
			})

			it('"Login" field initialized correctly', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('login_field-input')
				expect(node).toHaveValue('')
			})

			it('"System Role" field initialized correctly', async () => {
				const { getByTestId } = renderAndConfigure({
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('systemRoleId_selected-text')
					expect(node).toHaveTextContent('Select')
				})
			})

			it('"System Role" field initialized correctly (canEditRole: false)', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('systemRole_field-input')
				expect(node).toHaveValue('')
			})

			it('"System Role" field is disabled (canEditRole: false)', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('systemRole_field')
				expect(node).toHaveClass('TextField_disabled')
			})

			it('"Professionals" field initialized correctly', async () => {
				let { queryByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = queryByTestId('professionals_field-check-mark')
				expect(node).toBeNull()
			})

			it('"Professionals" field is disabled by default', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('professionals_field')
				expect(node).toHaveClass('CheckboxField_disabled')
			})

			it('"Organization" field initialized correctly (organizationId: 3)', async () => {
				const { getByTestId } = renderAndConfigure({
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('organizationId_selected-text')
					expect(node).toHaveTextContent(organization.name)
				})
			})

			it('"Organization" field initialized correctly (organizationId: undefined)', async () => {
				const { getByTestId } = renderAndConfigure({
					organizationId: undefined,
					userOrganizationId
				})

				const user = findWhere(User, { organizationId: userOrganizationId })

				await waitFor(() => {
					const node = getByTestId('organizationId_selected-text')
					expect(node).toHaveTextContent(user.organizationName)
				})
			})

			it('"Organization" field is disabled (isExpiredContact: true)', async () => {
				const { getByTestId } = renderAndConfigure({
					organizationId,
					userOrganizationId,
					isExpiredContact: true
				})

				await waitFor(() => {
					const node = getByTestId('organizationId_field')
					expect(node).toHaveClass('SelectField_disabled')
				})
			})

			it('"Organization" field is disabled (isPendingContact: true)', async () => {
				const { getByTestId } = renderAndConfigure({
					organizationId,
					userOrganizationId,
					isPendingContact: true
				})

				await waitFor(() => {
					const node = getByTestId('organizationId_field')
					expect(node).toHaveClass('SelectField_disabled')
				})
			})

			it('"Community" field initialized correctly (multiple communities)', async () => {
				const { getByTestId } = renderAndConfigure({
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('communityId_keyboard-search-input')
					expect(node).toBeVisible()
					expect(node).toHaveValue('')
				})
			})

			it('"Community" field initialized correctly (single community)', async () => {
				const { getByTestId } = renderAndConfigure({
					organizationId: 2964,
					userOrganizationId
				})

				const community = findWhere(Community, { organizationId: 2964 })

				await waitFor(() => {
					const node = getByTestId('communityId_selected-text')
					expect(node).toHaveTextContent(community.name)
				})
			})

			it('"Community" field is disabled (single community)', async () => {
				const { getByTestId } = renderAndConfigure({
					organizationId: 2964,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('communityId_field')
					expect(node).toHaveClass('no-pointer-events')
				})
			})

			it('"Community" field is disabled (isExpiredContact: true)', async () => {
				const { getByTestId } = renderAndConfigure({
					organizationId,
					userOrganizationId,
					isExpiredContact: true
				})

				await waitFor(() => {
					const node = getByTestId('communityId_field')
					expect(node).toHaveClass('SelectField_disabled')
				})
			})

			it('"Community" field is disabled (isPendingContact: true)', async () => {
				const { getByTestId } = renderAndConfigure({
					organizationId,
					userOrganizationId,
					isPendingContact: true
				})

				await waitFor(() => {
					const node = getByTestId('communityId_field')
					expect(node).toHaveClass('SelectField_disabled')
				})
			})

			it('"Profile photo" field is disabled by default', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('avatar_field-selected-file')
				expect(node).toHaveTextContent('File not chosen')
			})			
		})

		describe('Contact Info:', function () {
			it('"Use community address" field initialized correctly', async () => {
				const { getByTestId } = renderAndConfigure({
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('isCommunityAddressUsed_field-check-mark')
					expect(node).toBeVisible()
					expect(node).toBeInTheDocument()
				})
			})

			it('"Street" field initialized correctly', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('street_field-input')
				expect(node).toHaveValue('')
			})

			it('"City" field initialized correctly', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('city_field-input')
				expect(node).toHaveValue('')
			})

			it('"State" field initialized correctly', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('stateId_selected-text')
				expect(node).toHaveTextContent('Select State')
			})

			it('"Zip Code" field initialized correctly', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('zip_field-input')
				expect(node).toHaveValue('')
			})

			it('"Mobile Phone" field initialized correctly', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('mobilePhone_field-input')
				expect(node).toHaveValue('+1')
			})

			it('"Phone" field initialized correctly', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('phone_field-input')
				expect(node).toHaveValue('+1')
			})

			it('"Fax" field initialized correctly', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('fax_field-input')
				expect(node).toHaveValue('')
			})

			it('"Secure Email" field initialized correctly', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('secureMail_field-input')
				expect(node).toHaveValue('')
			})
		})

		describe('Settings:', function () {
			it('"Enable contact" field initialized correctly', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('enableContact_field-check-mark')
				expect(node).toBeInTheDocument()
				expect(node).toBeVisible()
			})

			it('"Enable contact" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = getByTestId('enableContact_field')
				expect(node).toHaveClass('CheckboxField_disabled')
			})

			it('"QA" field initialized correctly', async () => {
				let { queryByTestId } = render(
					<ContactForm organizationId={organizationId}/>
				)
				const node = queryByTestId('qaIncidentReports_field-check-mark')
				expect(node).toBeNull()
			})
		})
	})

	describe('Edit Contact. Initialization:', function () {
		const contactId = 44011
		const organizationId = 3
		const userOrganizationId = 3

		const contact = findWhere(ContactDetails, { id: contactId })

		describe('General Data:', function () {
			it('"First Name" field initialized correctly', async () => {
				const { getByTestId } = renderAndConfigure({
					contactId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('firstName_field-input')
					expect(node).toHaveValue(contact.firstName)
				})
			})

			it('"First Name" is disabled', async () => {
				const { getByTestId } = renderAndConfigure({
					contactId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('firstName_field')
					expect(node).toHaveClass('TextField_disabled')
				})
			})

			it('"Last Name" field initialized correctly', async () => {
				const { getByTestId } = renderAndConfigure({
					contactId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('lastName_field-input')
					expect(node).toHaveValue(contact.lastName)
				})
			})

			it('"Last Name" is disabled', async () => {
				const { getByTestId } = renderAndConfigure({
					contactId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('lastName_field')
					expect(node).toHaveClass('TextField_disabled')
				})
			})

			it('"Login" field initialized correctly', async () => {
				const { getByTestId } = renderAndConfigure({
					contactId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('login_field-input')
					expect(node).toHaveValue(contact.login)
				})
			})

			it('"Login" is disabled', async () => {
				const { getByTestId } = renderAndConfigure({
					contactId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('login_field')
					expect(node).toHaveClass('TextField_disabled')
				})
			})

			it('"System Role" field initialized correctly', async () => {
				const { getByTestId } = renderAndConfigure({
					contactId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('systemRoleId_selected-text')
					expect(node).toHaveTextContent(contact.systemRoleTitle)
				})
			})

			it('"System Role" field is disabled (isPersonReceivingServicesRole: true)', async () => {
				const { getByTestId } = renderAndConfigure({
					contactId: 56875,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('systemRoleId_field')
					expect(node).toHaveClass('SelectField_disabled')
				})
			})

			it('"Professionals" field initialized correctly', async () => {
				const { queryByTestId } = renderAndConfigure({
					contactId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = queryByTestId('professionals_field-check-mark')
					expect(node).toBeVisible()
					expect(node).toBeInTheDocument()
				})
			})

			it('"Professionals" field is disabled', async () => {
				const { getByTestId } = renderAndConfigure({
					contactId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('professionals_field')
					expect(node).toHaveClass('CheckboxField_disabled')
				})
			})

			it('"Organization" field initialized correctly', async () => {
				const { getByTestId } = renderAndConfigure({
					contactId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('organizationId_selected-text')
					expect(node).toHaveTextContent(contact.organizationName)
				})
			})

			it('"Organization" field is disabled', async () => {
				const { getByTestId } = renderAndConfigure({
					contactId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('organizationId_field')
					expect(node).toHaveClass('SelectField_disabled')
				})
			})

			it('"Community" field initialized correctly', async () => {
				const { getByTestId } = renderAndConfigure({
					contactId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('communityId_selected-text')
					expect(node).toHaveTextContent(contact.communityName)
				})
			})

			it('"Profile photo" field is disabled by default', async () => {
				const { getByTestId } = renderAndConfigure({
					contactId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('avatar_field-selected-file')
					expect(node).toHaveTextContent(contact.avatarName)
				})
			})
		})

		describe('Contact Info:', function () {
			it('"Use community address" field initialized correctly', async () => {
				const { queryByTestId } = renderAndConfigure({
					contactId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = queryByTestId('isCommunityAddressUsed_field-check-mark')

					if (contact.isCommunityAddressUsed) {
						expect(node).toBeInTheDocument()
						expect(node).toBeVisible()
					} else expect(node).toBeNull()
				})
			})

			it('"Street" field initialized correctly', async () => {
				const { getByTestId } = renderAndConfigure({
					contactId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('street_field-input')
					expect(node).toHaveValue(contact.address.street)
				})
			})

			it('"City" field initialized correctly', async () => {
				const { getByTestId } = renderAndConfigure({
					contactId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('city_field-input')
					expect(node).toHaveValue(contact.address.city)
				})
			})

			it('"State" field initialized correctly', async () => {
				const { getByTestId } = renderAndConfigure({
					contactId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('stateId_selected-text')
					expect(node).toHaveTextContent(contact.address.stateName)
				})
			})

			it('"Zip Code" field initialized correctly', async () => {
				const { getByTestId } = renderAndConfigure({
					contactId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('zip_field-input')
					expect(node).toHaveValue(contact.address.zip)
				})
			})

			describe('Use Community Address:', function () {
				const contactId = 1587
				const contact = findWhere(ContactDetails, { id: contactId })
				const community = findWhere(CommunityDetails, { id: contact.communityId })
				const state = findWhere(State, { id: community.stateId })

				it('"Use community address" field initialized correctly', async () => {
					const { queryByTestId } = renderAndConfigure({
						contactId,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						const node = queryByTestId('isCommunityAddressUsed_field-check-mark')
						expect(node).toBeInTheDocument()
						expect(node).toBeVisible()
					})
				})

				it('"Street" field initialized correctly', async () => {
					const { getByTestId } = renderAndConfigure({
						contactId,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						const node = getByTestId('street_field-input')
						expect(node).toHaveValue(community.street)
					})
				})

				it('"Street" field is disabled', async () => {
					const { getByTestId } = renderAndConfigure({
						contactId,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						const node = getByTestId('street_field')
						expect(node).toHaveClass('TextField_disabled')
					})
				})

				it('"City" field initialized correctly', async () => {
					const { getByTestId } = renderAndConfigure({
						contactId,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						const node = getByTestId('city_field-input')
						expect(node).toHaveValue(community.city)
					})
				})

				it('"City" field is disabled', async () => {
					const { getByTestId } = renderAndConfigure({
						contactId,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						const node = getByTestId('city_field')
						expect(node).toHaveClass('TextField_disabled')
					})
				})

				it('"State" field initialized correctly', async () => {
					const { getByTestId } = renderAndConfigure({
						contactId,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						const node = getByTestId('stateId_selected-text')
						expect(node).toHaveTextContent(state.label)
					})
				})

				it('"State" field is disabled', async () => {
					const { getByTestId } = renderAndConfigure({
						contactId,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						const node = getByTestId('stateId_field')
						expect(node).toHaveClass('SelectField_disabled')
					})
				})

				it('"Zip Code" field initialized correctly', async () => {
					const { getByTestId } = renderAndConfigure({
						contactId,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						const node = getByTestId('zip_field-input')
						expect(node).toHaveValue(community.zipCode)
					})
				})

				it('"Zip Code" field is disabled', async () => {
					const { getByTestId } = renderAndConfigure({
						contactId,
						organizationId,
						userOrganizationId
					})

					await waitFor(() => {
						const node = getByTestId('zip_field')
						expect(node).toHaveClass('TextField_disabled')
					})
				})
			})

			it('"Mobile Phone" field initialized correctly', async () => {
				const { getByTestId } = renderAndConfigure({
					contactId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('mobilePhone_field-input')
					expect(node).toHaveValue('+' + contact.mobilePhone)
				})
			})

			it('"Phone" field initialized correctly', async () => {
				const { getByTestId } = renderAndConfigure({
					contactId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('phone_field-input')
					expect(node).toHaveValue('+' + contact.phone)
				})
			})

			it('"Fax" field initialized correctly', async () => {
				const { getByTestId } = renderAndConfigure({
					contactId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('fax_field-input')
					expect(node).toHaveValue(contact.fax)
				})
			})

			it('"Secure Email" field initialized correctly', async () => {
				const { getByTestId } = renderAndConfigure({
					contactId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('secureMail_field-input')
					expect(node).toHaveValue(contact.secureMail)
				})
			})
		})

		describe('Settings:', function () {
			it('"Enable contact" field initialized correctly', async () => {
				const { queryByTestId } = renderAndConfigure({
					contactId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = queryByTestId('enableContact_field-check-mark')

					if (contact.enableContact) {
						expect(node).toBeInTheDocument()
						expect(node).toBeVisible()
					} else expect(node).toBeNull()
				})
			})

			it('"QA" field initialized correctly', async () => {
				const { queryByTestId } = renderAndConfigure({
					contactId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = queryByTestId('qaIncidentReports_field-check-mark')

					if (contact.qaIncidentReports) {
						expect(node).toBeInTheDocument()
						expect(node).toBeVisible()
					} else expect(node).toBeNull()
				})
			})
		})
	})

	describe('Expired Contact:', function () {
		const organizationId = 3

		describe('General Data:', function () {
			it('"First Name" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isExpiredContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('firstName_field')
				expect(node).toHaveClass('TextField_disabled')
			})

			it('"Last Name" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isExpiredContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('lastName_field')
				expect(node).toHaveClass('TextField_disabled')
			})

			it('"Login" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isExpiredContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('login_field')
				expect(node).toHaveClass('TextField_disabled')
			})

			it('"System Role" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						canEditRole
						isExpiredContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('systemRoleId_field')
				expect(node).toHaveClass('SelectField_disabled')
			})

			it('"Professionals" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isExpiredContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('professionals_field')
				expect(node).toHaveClass('CheckboxField_disabled')
			})

			it('"Organization" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isExpiredContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('organizationId_field')
				expect(node).toHaveClass('SelectField_disabled')
			})

			it('"Community" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isExpiredContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('communityId_field')
				expect(node).toHaveClass('SelectField_disabled')
			})

			it('"Profile photo" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isExpiredContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('avatar_field')
				expect(node).toHaveClass('FileField_disabled')
			})
		})

		describe('Contact Info:', function () {
			it('"Use community address" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isExpiredContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('isCommunityAddressUsed_field')
				expect(node).toHaveClass('CheckboxField_disabled')
			})

			it('"Street" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isExpiredContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('street_field')
				expect(node).toHaveClass('TextField_disabled')
			})

			it('"City" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isExpiredContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('city_field')
				expect(node).toHaveClass('TextField_disabled')
			})

			it('"State" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isExpiredContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('stateId_field')
				expect(node).toHaveClass('SelectField_disabled')
			})

			it('"Zip Code" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isExpiredContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('zip_field')
				expect(node).toHaveClass('TextField_disabled')
			})

			it('"Mobile Phone" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isExpiredContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('mobilePhone_field')
				expect(node).toHaveClass('PhoneField_disabled')
			})

			it('"Phone" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isExpiredContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('phone_field')
				expect(node).toHaveClass('PhoneField_disabled')
			})

			it('"Fax" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isExpiredContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('fax_field')
				expect(node).toHaveClass('TextField_disabled')
			})

			it('"Secure Email" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isExpiredContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('secureMail_field')
				expect(node).toHaveClass('TextField_disabled')
			})
		})

		describe('Settings:', function () {
			it('"Enable contact" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isExpiredContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('enableContact_field')
				expect(node).toHaveClass('CheckboxField_disabled')
			})

			it('"QA" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isExpiredContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('qaIncidentReports_field')
				expect(node).toHaveClass('CheckboxField_disabled')
			})
		})
	})

	describe('Pending Contact:', function () {
		const organizationId = 3

		describe('General Data:', function () {
			it('"First Name" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isPendingContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('firstName_field')
				expect(node).toHaveClass('TextField_disabled')
			})

			it('"Last Name" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isPendingContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('lastName_field')
				expect(node).toHaveClass('TextField_disabled')
			})

			it('"Login" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isPendingContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('login_field')
				expect(node).toHaveClass('TextField_disabled')
			})

			it('"System Role" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						canEditRole
						isPendingContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('systemRoleId_field')
				expect(node).toHaveClass('SelectField_disabled')
			})

			it('"Professionals" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isPendingContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('professionals_field')
				expect(node).toHaveClass('CheckboxField_disabled')
			})

			it('"Organization" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isPendingContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('organizationId_field')
				expect(node).toHaveClass('SelectField_disabled')
			})

			it('"Community" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isPendingContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('communityId_field')
				expect(node).toHaveClass('SelectField_disabled')
			})

			it('"Profile photo" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isPendingContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('avatar_field')
				expect(node).toHaveClass('FileField_disabled')
			})
		})

		describe('Contact Info:', function () {
			it('"Use community address" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isPendingContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('isCommunityAddressUsed_field')
				expect(node).toHaveClass('CheckboxField_disabled')
			})

			it('"Street" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isPendingContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('street_field')
				expect(node).toHaveClass('TextField_disabled')
			})

			it('"City" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isPendingContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('city_field')
				expect(node).toHaveClass('TextField_disabled')
			})

			it('"State" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isPendingContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('stateId_field')
				expect(node).toHaveClass('SelectField_disabled')
			})

			it('"Zip Code" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isPendingContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('zip_field')
				expect(node).toHaveClass('TextField_disabled')
			})

			it('"Mobile Phone" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isPendingContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('mobilePhone_field')
				expect(node).toHaveClass('PhoneField_disabled')
			})

			it('"Phone" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isPendingContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('phone_field')
				expect(node).toHaveClass('PhoneField_disabled')
			})

			it('"Fax" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isPendingContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('fax_field')
				expect(node).toHaveClass('TextField_disabled')
			})

			it('"Secure Email" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isPendingContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('secureMail_field')
				expect(node).toHaveClass('TextField_disabled')
			})
		})

		describe('Settings:', function () {
			it('"Enable contact" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isPendingContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('enableContact_field')
				expect(node).toHaveClass('CheckboxField_disabled')
			})

			it('"QA" field is disabled', async () => {
				let { getByTestId } = render(
					<ContactForm
						isPendingContact
						organizationId={organizationId}
					/>
				)
				const node = getByTestId('qaIncidentReports_field')
				expect(node).toHaveClass('CheckboxField_disabled')
			})
		})
	})

	describe('Create Contact for Client:', function () {
		const clientId = 61782
		const organizationId = 3
		const userOrganizationId = 3

		const client = findWhere(ClientDetails, { id: clientId })

		describe('General Data:', function () {
			it('"First Name" field initialized correctly', async () => {
				const { getByTestId } = renderAndConfigure({
					clientId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('firstName_field-input')
					expect(node).toHaveValue(client.firstName)
				})
			})

			it('"Last Name" field initialized correctly', async () => {
				const { getByTestId } = renderAndConfigure({
					clientId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('lastName_field-input')
					expect(node).toHaveValue(client.lastName)
				})
			})

			it('"Organization" field initialized correctly', async () => {
				const { queryByTestId } = renderAndConfigure({
					clientId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = queryByTestId('organizationId_selected-text')
					expect(node).toHaveTextContent(client.organization)
				})
			})

			it('"Community" field initialized correctly', async () => {
				const { queryByTestId } = renderAndConfigure({
					clientId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = queryByTestId('communityId_selected-text')
					expect(node).toHaveTextContent(client.community)
				})
			})
		})

		describe('Contact Info:', function () {
			it('"Mobile Phone" field initialized correctly', async () => {
				const { getByTestId } = renderAndConfigure({
					clientId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('mobilePhone_field-input')
					expect(node).toHaveValue('+' + client.cellPhone)
				})
			})

			it('"Phone" field initialized correctly', async () => {
				const { getByTestId } = renderAndConfigure({
					clientId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('phone_field-input')
					expect(node).toHaveValue('+' + client.phone)
				})
			})

			it('"Street" field initialized correctly', async () => {
				const { getByTestId } = renderAndConfigure({
					clientId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('street_field-input')
					expect(node).toHaveValue(client.address.street)
				})
			})

			it('"City" field initialized correctly', async () => {
				const { getByTestId } = renderAndConfigure({
					clientId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('city_field-input')
					expect(node).toHaveValue(client.address.city)
				})
			})

			it('"State" field initialized correctly', async () => {
				const { getByTestId } = renderAndConfigure({
					clientId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('stateId_selected-text')
					expect(node).toHaveTextContent(client.address.stateName)
				})
			})

			it('"Zip Code" field initialized correctly', async () => {
				const { getByTestId } = renderAndConfigure({
					clientId,
					organizationId,
					userOrganizationId
				})

				await waitFor(() => {
					const node = getByTestId('zip_field-input')
					expect(node).toHaveValue(client.address.zip)
				})
			})
		})
	})

	afterAll(() => {
		server.close()
	})
})