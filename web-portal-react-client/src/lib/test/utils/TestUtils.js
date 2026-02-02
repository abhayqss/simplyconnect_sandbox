import React from 'react'

import { waitFor, render } from 'lib/test-utils'

import { noop } from 'lib/utils/FuncUtils'

export function runTest(name, rendererFn, init, expect) {
	it(name, () => {
		let config = render(rendererFn())

		if (init) init(config)

		expect(config)
	})
}

export function runAsyncTest(name, rendererFn, init, expect, options) {
	it(name, async () => {
		const config = render(rendererFn())

		if (init) await init(config)

		await waitFor(() => {
			expect(config)
		})
	})
}

export class Test {
	#task

	hasTask() {
		return !!this.#task
	}

	setTask(taskFn = noop) {
		this.#task = taskFn
	}

	getTask() {
		return this.#task
	}

	run() {}
}

export class GenericTest extends Test {
	#name
	#renderer
	#init = noop
	#options

	/*
	* @required renderer
	* @optional init
	* @optional options
	* */
	constructor(name, renderer, init = noop, options = {}) {
		super()
		this.#name = name
		this.#renderer = renderer
		this.#options = options

		if (typeof init === 'function') {
			this.#init = init
		}

		if (typeof init === 'object') {
			this.#options = init
		}
	}

	setRenderer(renderer) {
		this.#renderer = renderer
	}

	getRenderer() {
		return this.#renderer
	}

	setInit(init = noop) {
		this.#init = init
	}

	getInit() {
		return this.#init
	}

	setOptions(options) {
		this.#options = options
	}

	getOptions() {
		return this.#options
	}

	set name(text) {
		this.#name = text
	}

	get name() {
		return this.#name
	}

	run(renderer, options) {
		if (this.hasTask()) {
			this.getTask()(renderer, options)
		}
	}

	expect(expect) {
		this.setTask(() => {
			runTest(this.name, this.#renderer, this.#init, expect)
		})

		return this
	}
}

export class GenericAsyncTest extends GenericTest {
	expect(expect) {
		this.setTask(() => {
			runAsyncTest(
				this.name,
				this.getRenderer(),
				this.getInit(),
				expect,
				this.getOptions()
			)
		})

		return this
	}
}

export class TestRunner {
	#tests = []

	constructor(tests = []) {
		this.#tests = [...tests]
	}

	add(test) {
		this.#tests.push(test)
	}

	clear() {
		this.#tests = []
	}

	run() {
		for (const test of this.#tests) {
			test.run()
		}
	}
}