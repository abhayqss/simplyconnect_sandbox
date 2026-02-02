import React from 'react'

import Dates, { add } from 'date-arithmetic'

import { rest } from 'msw'
import { setupServer } from 'msw/node'
import { waitFor, render } from 'lib/test-utils'

import Response from 'lib/mock/server/Response'

import {
	Client,
	NoteType,
	GroupedEventType
} from 'lib/mock/db/DB'

import {
	format,
	formats
} from 'lib/utils/DateUtils'

import EventNoteFilter from './EventNoteFilter'

const BASE_URL = 'https://dev.simplyconnect.me/web-portal-mock-backend'

const server = setupServer(
	rest.get(`${BASE_URL}/authorized-directory/clients`, (req, res, ctx) => {
		return res(
			ctx.json(Response.success(Client))
		)
	}),
	rest.get(`${BASE_URL}/directory/note-types`, (req, res, ctx) => {
		return res(
			ctx.json(Response.success(NoteType))
		)
	}),
	rest.get(`${BASE_URL}/authorized-directory/grouped-event-types`, (req, res, ctx) => {
		return res(
			ctx.json(Response.success(GroupedEventType))
		)
	})
)

function threeMonthsAgo() {
	return add(new Date(), -3, 'month')
}

describe('<EventNoteFilter>:', () => {
	beforeAll(() => {
		server.listen()
	})

	describe('All fields are visible on UI:', function () {
		it('Client Name', () => {
			const { getByTestId } = render(<EventNoteFilter/>)

			const node = getByTestId('clientId_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Event Type', () => {
			const { getByTestId } = render(<EventNoteFilter/>)

			const node = getByTestId('eventTypeId_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Note Type', () => {
			const { getByTestId } = render(<EventNoteFilter/>)

			const node = getByTestId('noteTypeId_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Date From', () => {
			const { getByTestId } = render(<EventNoteFilter/>)

			const node = getByTestId('fromDate_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Date To', () => {
			const { getByTestId } = render(<EventNoteFilter/>)

			const node = getByTestId('toDate_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Only events with incident report', () => {
			const { getByTestId } = render(<EventNoteFilter/>)

			const node = getByTestId('onlyEventsWithIR_field')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Clear Button', () => {
			const { getByTestId } = render(<EventNoteFilter/>)

			const node = getByTestId('clear-btn')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})

		it('Apply Button', () => {
			const { getByTestId } = render(<EventNoteFilter/>)

			const node = getByTestId('apply-btn')
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})
	})

	describe('Some fields are hidden on UI:', function () {
		it('Client Name', () => {
			const { queryByTestId } = render(
				<EventNoteFilter
					clientId={1106}
				/>
			)

			const node = queryByTestId('clientId_field')
			expect(node).toBe(null)
		})

		it('Note Type', () => {
			const { queryByTestId } = render(
				<EventNoteFilter
					areNotesExcluded
				/>
			)

			const node = queryByTestId('noteTypeId_field')
			expect(node).toBe(null)
		})

		it('Only events with incident report', () => {
			const { queryByTestId } = render(
				<EventNoteFilter
					areNotesExcluded
				/>
			)

			const node = queryByTestId('onlyEventsWithIR_field')
			expect(node).toBe(null)
		})
	})

	describe('Default Initialization:', function () {
		it('Client Name', () => {
			const { queryByTestId } = render(<EventNoteFilter/>)

			const node = queryByTestId('clientId_selected-text')

			expect(node).toBe(null)
		})

		it('Event Type', () => {
			const { queryByTestId } = render(<EventNoteFilter/>)

			const node = queryByTestId('eventTypeId_selected-text')

			expect(node).toHaveTextContent('Select Event Type')
		})

		it('Note Type', () => {
			const { queryByTestId } = render(<EventNoteFilter/>)

			const node = queryByTestId('noteTypeId_selected-text')

			expect(node).toBe(null)
		})

		it('Date From', async () => {
			const { getByTestId } = render(
				<EventNoteFilter
					clientId={1106}
				/>
			)

			await waitFor(() => {
				const node = getByTestId('fromDate_field-input')
				expect(node).toHaveValue(format(threeMonthsAgo(), formats.americanMediumDate))
			})
		})

		it('Date To', async () => {
			const { getByTestId } = render(
				<EventNoteFilter
					clientId={1106}
				/>
			)

			await waitFor(() => {
				const node = getByTestId('toDate_field-input')
				expect(node).toHaveValue(format(new Date(), formats.americanMediumDate))
			})
		})

		it('Only events with incident report', () => {
			const { queryByTestId } = render(<EventNoteFilter/>)

			const node = queryByTestId('onlyEventsWithIR_field-check-mark')
			expect(node).toBe(null)
		})
	})

	afterAll(() => {
		server.close()
	})
})