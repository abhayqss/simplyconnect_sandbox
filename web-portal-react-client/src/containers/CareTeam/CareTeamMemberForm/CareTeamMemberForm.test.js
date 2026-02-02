import React from 'react'

import {
	where,
	pluck,
	filter,
	findWhere
} from 'underscore'

import { rest } from 'msw'
import { setupServer } from 'msw/node'

import { within } from 'lib/test-utils'

import {
	TestRunner,
	GenericAsyncTest
} from 'lib/test/utils/TestUtils'

import {
	FieldType,
	FieldTestFactory,
	FieldAsyncTestFactory
} from 'lib/test/utils/FormTestUtils'

import Response from 'lib/mock/server/Response'

import {
	User,
	Contact,
	SystemRole,
	Organization,
	Responsibility,
	NotificationType,
	GroupedEventType,
	CareTeamMemberDetails,
	DefaultNotificationPreferences
} from 'lib/mock/db/DB'

import {
	first
} from 'lib/utils/ArrayUtils'

import CareTeamMemberForm from './CareTeamMemberForm'

const BASE_URL = 'https://dev.simplyconnect.me/web-portal-mock-backend'

const server = setupServer(
	rest.get(`${BASE_URL}/authorized-directory/care-team/notification-types`, (req, res, ctx) => {
		return res(ctx.json(Response.success(NotificationType)))
	}),
	rest.get(`${BASE_URL}/authorized-directory/grouped-event-types`, (req, res, ctx) => {
		return res(ctx.json(Response.success(GroupedEventType)))
	}),
	rest.get(`${BASE_URL}/authorized-directory/care-team/client-member-roles`, (req, res, ctx) => {
		return res(ctx.json(Response.success(SystemRole)))
	}),
	rest.get(`${BASE_URL}/authorized-directory/care-team/community-member-roles`, (req, res, ctx) => {
		return res(ctx.json(Response.success(SystemRole)))
	}),
	rest.get(`${BASE_URL}/authorized-directory/care-team/responsibilities`, (req, res, ctx) => {
		return res(ctx.json(Response.success(Responsibility)))
	}),
	rest.get(`${BASE_URL}/care-team-members/contacts`, (req, res, ctx) => {
		return res(ctx.json(Response.success(Contact)))
	}),
	rest.get(`${BASE_URL}/care-team-members/contacts/organizations`, (req, res, ctx) => {
		return res(ctx.json(Response.success(where(Organization, { id: 7 }))))
	}),
	rest.get(`${BASE_URL}/care-team-members/:memberId`, (req, res, ctx) => {
		return res(ctx.json(Response.success(findWhere(CareTeamMemberDetails, { id: +req.params.memberId }))))
	}),
	rest.get(`${BASE_URL}/authorized-directory/care-team/default-notification-preferences`, (req, res, ctx) => {
		const careTeamRoleId = +req.url.searchParams.get('careTeamRoleId')
		const o = findWhere(DefaultNotificationPreferences, { careTeamRoleId })
		return res(ctx.json(Response.success(o.preferences)))
	})
)

describe('<CareTeamMemberForm>:', function () {
	beforeAll(() => {
		server.listen()
	})

	beforeEach(() => {
		jest.clearAllMocks()
	})

	describe('All fields are visible:', function () {
		const organizationId = 3

		describe('General:', function () {
			const runner = new TestRunner()

			const factory = new FieldTestFactory(() => (
				<CareTeamMemberForm
					organizationId={organizationId}
					communityIds={[]}
				/>
			))

			function FieldToBeVisibleTest(title, name) {
				return factory.create(name, title).expectToBeVisible()
			}

			runner.add(FieldToBeVisibleTest('Organization', 'employeeOrganizationId'))
			runner.add(FieldToBeVisibleTest('Contact Name', 'employeeId'))
			runner.add(FieldToBeVisibleTest('Role', 'roleId'))
			runner.add(FieldToBeVisibleTest('Description', 'description'))
			runner.run()

			describe('Community is Specified:', function () {
				const communityId = 143845

				const runner = new TestRunner()

				const factory = new FieldTestFactory(() => (
					<CareTeamMemberForm
						communityId={communityId}
						organizationId={organizationId}
						communityIds={[]}
					/>
				))

				runner.add(factory.create('includeInFaceSheet', 'Include contact in the facesheet document').expectNotToBeInDocument())
				runner.run()
			})

			describe('Client is Specified:', function () {
				const clientId = 61782

				const runner = new TestRunner()

				const factory = new FieldTestFactory(() => (
					<CareTeamMemberForm
						clientId={clientId}
						organizationId={organizationId}
						communityIds={[]}
					/>
				))

				runner.add(factory.create('includeInFaceSheet', 'Include contact in the facesheet document').expectToBeVisible())
				runner.run()
			})
		})

		const userOrganizationId = 3

		function Renderer({ clientId, communityId }) {
			return () => (
				<CareTeamMemberForm
					clientId={clientId}
					communityId={communityId}
					organizationId={organizationId}
					communityIds={[]}
				/>
			)
		}

		async function init(config) {
			const { store, userEvent } = config

			const user = findWhere(User, { organizationId: userOrganizationId })
			store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

			await config.findByText('Denise Weber')
			await config.findByTestId('employeeId_multi-select')
			await userEvent.click(config.getByTestId('employeeId_search-input'))
			await userEvent.click(config.getByText('Denise Weber'))

			await within(config.getByTestId('roleId_options')).findByText('Behavioral Health')
			await userEvent.click(config.getByTestId('roleId_selected-text'))
			await userEvent.click(within(config.getByTestId('roleId_options')).getByText('Behavioral Health'))

			await config.findByText('Notification Preferences')
		}

		const options = { isReduxConnected: true }

		function LabelToBeVisibleTest(title, renderer) {
			return new GenericAsyncTest(`"${title}" label is visible`, renderer, init, options)
				.expect(config => {
					const node = config.getByText(title)
					expect(node).toBeInTheDocument()
					expect(node).toBeVisible()
				})
		}

		describe('Notification Preferences:', function () {
			describe('Community specified:', function () {
				const communityId = 143845

				const runner = new TestRunner()

				const renderer = Renderer({ communityId })

				runner.add(LabelToBeVisibleTest('All Events', renderer))

				const factory = new FieldAsyncTestFactory(renderer, options)

				runner.add(factory.create('all-responsibility', 'Responsibility', init).expectToBeVisible())
				runner.add(factory.create('all-channel', 'Channel', init).expectToBeVisible())

				runner.run()
			})

			describe('Client specified:', function () {
				const clientId = 4054

				const runner = new TestRunner()

				const renderer = Renderer({ clientId })

				runner.add(LabelToBeVisibleTest('All Events', renderer))

				const factory = new FieldAsyncTestFactory(renderer, options)

				runner.add(factory.create('all-responsibility', 'Responsibility', init).expectToBeVisible())
				runner.add(factory.create('all-channel', 'Channel', init).expectToBeVisible())

				runner.run()
			})
		})

		first(GroupedEventType, 3).forEach(eventGroup => {
			describe(`${eventGroup.title}:`, function () {
				describe('Community specified:', function () {
					const communityId = 143845

					const runner = new TestRunner()

					const renderer = Renderer({ communityId })

					const factory = new FieldAsyncTestFactory(renderer, options)

					eventGroup.eventTypes.forEach(eventType => {
						runner.add(LabelToBeVisibleTest(eventType.title, renderer))
						runner.add(factory.create(`${eventType.id}-responsibility`, 'Responsibility', init).expectToBeVisible())
						runner.add(factory.create(`${eventType.id}-channel`, 'Channel', init).expectToBeVisible())
					})

					runner.run()
				})

				describe('Client specified:', function () {
					const clientId = 4054

					const runner = new TestRunner()

					const renderer = Renderer({ clientId })

					const factory = new FieldAsyncTestFactory(renderer, options)

					eventGroup.eventTypes.forEach(eventType => {
						runner.add(LabelToBeVisibleTest(eventType.title, renderer))
						runner.add(factory.create(`${eventType.id}-responsibility`, 'Responsibility', init).expectToBeVisible())
						runner.add(factory.create(`${eventType.id}-channel`, 'Channel', init).expectToBeVisible())
					})

					runner.run()
				})
			})
		})
	})

	describe('Fields marked as required:', function () {
		const organizationId = 3

		describe('General:', function () {
			const runner = new TestRunner()

			const factory = new FieldTestFactory(() => (
				<CareTeamMemberForm
					communityIds={[]}
					organizationId={organizationId}
				/>
			))

			function FieldToBeRequiredTest(name, title) {
				return factory.create(name, title).expectToBeRequired()
			}

			runner.add(FieldToBeRequiredTest('employeeOrganizationId', 'Organization'))
			runner.add(FieldToBeRequiredTest('employeeId', 'Contact Name'))
			runner.add(FieldToBeRequiredTest('roleId', 'Role'))

			runner.run()
		})
	})

	describe('Add new care Team Member. Default Field values:', function () {
		const organizationId = 3
		const userOrganizationId = 3
		const careTeamRoleId = 6

		function Renderer({ clientId, communityId }) {
			return () => (
				<CareTeamMemberForm
					clientId={clientId}
					communityId={communityId}
					organizationId={organizationId}
					communityIds={[]}
				/>
			)
		}

		async function init(config) {
			const { store, userEvent } = config

			const user = findWhere(User, { organizationId: userOrganizationId })
			store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })

			await config.findByText('Denise Weber')
			await config.findByTestId('employeeId_multi-select')
			await userEvent.click(config.getByTestId('employeeId_search-input'))
			await userEvent.click(config.getByText('Denise Weber'))

			await within(config.getByTestId('roleId_options')).findByText('Behavioral Health')
			await userEvent.click(config.getByTestId('roleId_selected-text'))
			await userEvent.click(within(config.getByTestId('roleId_options')).getByText('Behavioral Health'))

			await config.findByText('Notification Preferences')
		}

		const options = { isReduxConnected: true }

		const {
			preferences = []
		} = findWhere(DefaultNotificationPreferences, { careTeamRoleId }) ?? {}

		describe('Community specified:', function () {
			const communityId = 143845

			const renderer = Renderer({ communityId })

			const factory = FieldAsyncTestFactory.instance(FieldType.SELECT, renderer, options)

			first(GroupedEventType, 3).forEach(eventGroup => {
				describe(`${eventGroup.title}:`, function () {
					eventGroup.eventTypes.forEach(eventType => {
						const preference = findWhere(preferences, { eventTypeId: eventType.id })

						const responsibility = findWhere(Responsibility, { name: preference.responsibilityName })

						const notificationTypes = filter(NotificationType, o => preference.channels?.includes(o.name))
						const notificationTypesText = pluck(notificationTypes, 'title').join(', ')

						describe(`${eventType.title}:`, function () {
							const runner = new TestRunner()

							runner.add(factory.create(`${eventType.id}-responsibility`, 'Responsibility', init).expectToHaveValue(responsibility.title))
							runner.add(factory.create(`${eventType.id}-channel`, 'Channel', init).expectToHaveValue(notificationTypesText || 'Nothing selected'))

							runner.run()
						})
					})
				})
			})
		})

		describe('Client specified:', function () {
			const clientId = 4054

			const renderer = Renderer({ clientId })

			const factory = FieldAsyncTestFactory.instance(FieldType.SELECT, renderer, options)

			first(GroupedEventType, 3).forEach(eventGroup => {
				describe(`${eventGroup.title}:`, function () {
					eventGroup.eventTypes.forEach(eventType => {
						const preference = findWhere(preferences, { eventTypeId: eventType.id })

						const responsibility = findWhere(Responsibility, { name: preference.responsibilityName })

						const notificationTypes = filter(NotificationType, o => preference.channels?.includes(o.name))
						const notificationTypesText = pluck(notificationTypes, 'title').join(', ')

						describe(`${eventType.title}:`, function () {
							const runner = new TestRunner()

							runner.add(factory.create(`${eventType.id}-responsibility`, 'Responsibility', init).expectToHaveValue(responsibility.title))
							runner.add(factory.create(`${eventType.id}-channel`, 'Channel', init).expectToHaveValue(notificationTypesText || 'Nothing selected'))

							runner.run()
						})
					})
				})
			})
		})
	})

	describe('Edit Care Team Member. Initialization:', function () {
		const organizationId = 3
		const userOrganizationId = 3
		const careTeamRoleId = 6
		const careTeamMemberId = 26660

		function Renderer({ clientId, communityId }) {
			return () => (
				<CareTeamMemberForm
					clientId={clientId}
					communityId={communityId}
					memberId={careTeamMemberId}
					organizationId={organizationId}
					communityIds={[]}
				/>
			)
		}

		async function init(config) {
			const { store } = config

			const user = findWhere(User, { organizationId: userOrganizationId })
			store.dispatch({ type: 'LOGIN_SUCCESS', payload: user })
			store.dispatch({ type: 'LOAD_CARE_TEAM_MEMBER_DETAILS_SUCCESS', payload: member })

			await config.findByText('Notification Preferences')
		}

		const options = { isReduxConnected: true }

		const member = findWhere(CareTeamMemberDetails, { id: careTeamMemberId })

		const preferences = member.notificationsPreferences

		describe('Community specified:', function () {
			const communityId = 143845

			const renderer = Renderer({ communityId })

			const factory = FieldAsyncTestFactory.instance(FieldType.SELECT, renderer, options)

			first(GroupedEventType, 3).forEach(eventGroup => {
				describe(`${eventGroup.title}:`, function () {
					eventGroup.eventTypes.forEach(eventType => {
						const preference = findWhere(preferences, { eventTypeId: eventType.id })

						const responsibility = findWhere(Responsibility, { name: preference.responsibilityName })

						const notificationTypes = filter(NotificationType, o => preference.channels?.includes(o.name))
						const notificationTypesText = pluck(notificationTypes, 'title').join(', ')

						describe(`${eventType.title}:`, function () {
							const runner = new TestRunner()

							runner.add(factory.create(`${eventType.id}-responsibility`, 'Responsibility', init).expectToHaveValue(responsibility.title))
							runner.add(factory.create(`${eventType.id}-channel`, 'Channel', init).expectToHaveValue(notificationTypesText || 'Nothing selected'))

							runner.run()
						})
					})
				})
			})
		})

		describe('Client specified:', function () {
			const clientId = 4054

			const renderer = Renderer({ clientId })

			const factory = FieldAsyncTestFactory.instance(FieldType.SELECT, renderer, options)

			first(GroupedEventType, 3).forEach(eventGroup => {
				describe(`${eventGroup.title}:`, function () {
					eventGroup.eventTypes.forEach(eventType => {
						const preference = findWhere(preferences, { eventTypeId: eventType.id })

						const responsibility = findWhere(Responsibility, { name: preference.responsibilityName })

						const notificationTypes = filter(NotificationType, o => preference.channels?.includes(o.name))
						const notificationTypesText = pluck(notificationTypes, 'title').join(', ')

						describe(`${eventType.title}:`, function () {
							const runner = new TestRunner()

							runner.add(factory.create(`${eventType.id}-responsibility`, 'Responsibility', init).expectToHaveValue(responsibility.title))
							runner.add(factory.create(`${eventType.id}-channel`, 'Channel', init).expectToHaveValue(notificationTypesText || 'Nothing selected'))

							runner.run()
						})
					})
				})
			})
		})
	})

	afterAll(() => {
		server.close()
	})
})
