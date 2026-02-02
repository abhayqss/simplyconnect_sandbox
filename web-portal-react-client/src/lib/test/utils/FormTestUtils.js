import React from 'react'

import { promise } from 'lib/utils/Utils'
import { noop } from 'lib/utils/FuncUtils'

import {
	Test,
	runTest,
	runAsyncTest
} from './TestUtils'

function FieldToBeVisibleTest(name, title, description, defaultRendererFn, defaultInit) {
	const defaultDescription = 'field is visible'
	return function (rendererFn = defaultRendererFn, init = defaultInit) {
		runTest(`"${title}" ${description ?? defaultDescription}`, rendererFn, init, config => {
			const node = config.getByTestId(`${name}_field`)
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		})
	}
}

function FieldNotToBeVisibleTest(name, title, description, defaultRendererFn, defaultInit) {
	const defaultDescription = 'field is not visible'
	return function (rendererFn = defaultRendererFn, init = defaultInit) {
		runTest(`"${title}" ${description ?? defaultDescription}`, rendererFn, init, config => {
			const node = config.queryByTestId(`${name}_field`)
			expect(node).not.toBeVisible()
		})
	}
}

function FieldNotToBeInDocumentTest(name, title, description, defaultRendererFn, defaultInit) {
	const defaultDescription = 'field is not in the document and not visible'
	return function (rendererFn = defaultRendererFn, init = defaultInit) {
		runTest(`"${title}" ${description ?? defaultDescription}`, rendererFn, init, config => {
			const node = config.queryByTestId(`${name}_field`)
			expect(node).not.toBeInTheDocument()
		})
	}
}

function FieldToBeVisibleAsyncTest(name, title, description, defaultRendererFn, defaultInit = promise(), options) {
	const defaultDescription = 'field is visible'
	return function (rendererFn = defaultRendererFn, init = defaultInit) {
		runAsyncTest(`"${title}" ${description ?? defaultDescription}`, rendererFn, init, config => {
			const node = config.getByTestId(`${name}_field`)
			expect(node).toBeInTheDocument()
			expect(node).toBeVisible()
		}, options)
	}
}

function FieldToBeRequiredTest(name, title, description, defaultRendererFn, defaultInit) {
	const defaultDescription = 'field is required'
	return function (rendererFn = defaultRendererFn, init = defaultInit) {
		runTest(`"${title}" ${description ?? defaultDescription}`, rendererFn, init, config => {
			const node = config.getByTestId(`${name}_field-label`)
			expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
		})
	}
}

function FieldToBeRequiredAsyncTest(name, title, description, defaultRendererFn, defaultInit = promise(), options) {
	const defaultDescription = 'field is required'
	return function (rendererFn = defaultRendererFn, init = defaultInit) {
		runAsyncTest(`"${title}" ${description ?? defaultDescription}`, rendererFn, init, config => {
			const node = config.getByTestId(`${name}_field-label`)
			expect(String(node.innerHTML).endsWith('*')).toBeTruthy()
		}, options)
	}
}

export function testField(name, title) {
	return function (defaultRendererFn = noop, defaultInit = noop) {
		return {
			expectToBeVisible(description) {
				return FieldToBeVisibleTest(name, title, description, defaultRendererFn, defaultInit)
			},
			expectNotToBeVisible(description) {
				return FieldNotToBeVisibleTest(name, title, description, defaultRendererFn, defaultInit)
			},
			expectNotToBeInDocument(description) {
				return FieldNotToBeInDocumentTest(name, title, description, defaultRendererFn, defaultInit)
			},
			expectToBeRequired(description) {
				return FieldToBeRequiredTest(name, title, description, defaultRendererFn, defaultInit)
			}
		}
	}
}

export function testFieldAsync(name, title) {
	return function (defaultRendererFn = noop, defaultInit = noop, options = {}) {
		return {
			expectToBeVisible(description) {
				return FieldToBeVisibleAsyncTest(name, title, description, defaultRendererFn, defaultInit, options)
			},
			expectToBeRequired(description) {
				return FieldToBeRequiredAsyncTest(name, title, description, defaultRendererFn, defaultInit, options)
			}
		}
	}
}

export function testTextField(name, title) {
	return function (defaultRendererFn = noop, defaultInit = noop) {
		return {
			...testField(name, title)(defaultRendererFn, defaultInit),
			expectToHaveValue(value, description) {
				const defaultDescription = `field has value: ${value}`
				return function (rendererFn = defaultRendererFn, init = defaultInit) {
					runTest(`"${title}" ${description ?? defaultDescription}`, rendererFn, init, config => {
						const node = config.getByTestId(`${name}_field-input`)
						expect(node).toHaveValue(value)
					})
				}
			}
		}
	}
}

export function testTextFieldAsync(name, title) {
	return function (defaultRenderFn, defaultInit = noop, options = {}) {
		return {
			toHaveValue(value, description) {
				const defaultDescription = `field has value ${value}`
				return function (renderFn = defaultRenderFn, init = defaultInit) {
					runAsyncTest(`"${title}" ${description ?? defaultDescription}`, renderFn, init, config => {
						let node = config.getByTestId(`${name}_field-input`)
						expect(node).toHaveValue(value)
					}, options)
				}
			}
		}
	}
}

export function testCheckBoxField(name, title) {
	return function (defaultRendererFn = noop, defaultInit = noop) {
		return {
			...testField(name, title)(defaultRendererFn, defaultInit),
			expectToHaveValue(value, description) {
				const defaultDescription = `field has value: ${value}`
				return function (rendererFn = defaultRendererFn, init = defaultInit) {
					runTest(`"${title}" ${description ?? defaultDescription}`, rendererFn, init, config => {
						const node = config.queryByTestId(`${name}_field-check-mark`)

						if (value) {
							expect(node).toBeInTheDocument()
							expect(node).toBeVisible()
						} else expect(node).toBeNull()
					})
				}
			}
		}
	}
}

export function testCheckBoxFieldAsync(name, title) {
	return function (defaultRenderFn, defaultInit, options = {}) {
		return {
			toHaveValue(value, description) {
				const defaultDescription = `field has value ${value}`
				return function (renderFn = defaultRenderFn, init = defaultInit) {
					runAsyncTest(`"${title}" ${description ?? defaultDescription}`, renderFn, init, config => {
						let node = config.getByTestId(`${name}_field-check-mark`)

						if (value) {
							expect(node).toBeInTheDocument()
							expect(node).toBeVisible()
						} else expect(node).toBeNull()
					}, options)
				}
			}
		}
	}
}

export function testSelectField(name, title) {
	return function (defaultRendererFn = noop, defaultInit = noop) {
		return {
			...testField(name, title)(defaultRendererFn, defaultInit),
			expectToHaveSelectedText(value, description) {
				const defaultDescription = `field has selected text: "${value}"`
				return function (rendererFn = defaultRendererFn, init = defaultInit) {
					runTest(`"${title}" ${description ?? defaultDescription}`, rendererFn, init, config => {
						const node = config.getByTestId(`${name}_selected-text`)
						expect(node).toHaveTextContent(value)
					})
				}
			}
		}
	}
}

export function testSelectFieldAsync(name, title) {
	return function (defaultRenderFn, defaultInit, options = {}) {
		return {
			expectToHaveSelectedText(value, description) {
				const defaultDescription = `field has selected text: "${value}"`
				return function (renderFn = defaultRenderFn, init = defaultInit) {
					runAsyncTest(`"${title}" ${description ?? defaultDescription}`, renderFn, init, config => {
						let node = config.getByTestId(`${name}_selected-text`)
						expect(node).toHaveTextContent(value)
					}, options)
				}
			}
		}
	}
}

export const FieldType = {
	TEXT: 'TEXT',
	SELECT: 'SELECT',
	CHECKBOX: 'CHECKBOX',
}

export class Field {
	#name
	#title
	#type

	constructor(name, title, type) {
		this.#name = name
		this.#title = title

		if (type) this.#type = type
	}

	get name() {
		return this.#name
	}

	get type() {
		return this.#type
	}

	get title() {
		return this.#title
	}
}

export class TextField extends Field {
	constructor(name, title) {
		super(name, title, FieldType.TEXT)
	}
}

export class CheckBoxField extends Field {
	constructor(name, title) {
		super(name, title, FieldType.CHECKBOX)
	}
}

export class SelectField extends Field {
	constructor(name, title) {
		super(name, title, FieldType.SELECT)
	}
}

export class FieldTest extends Test {
	#field
	#renderer
	#init = noop
	#options
	#description

	/*
	* @required field
	* @required renderer
	* @optional init
	* @optional options
	* */
	constructor(field, renderer, init = noop, options = {}) {
		super()
		this.#field = field
		this.#renderer = renderer
		this.#options = options

		if (typeof init === 'function') {
			this.#init = init
		}

		if (typeof init === 'object') {
			this.#options = init
		}
	}

	get field() {
		return this.#field
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

	set description(text) {
		this.#description = text
	}

	get description() {
		return this.#description
	}

	run(renderer, options) {
		if (this.hasTask()) {
			this.getTask()(renderer, options)
		}
	}

	expectToBeVisible() {
		this.setTask(
			testField(
				this.field.name, this.field.title
			)(this.#renderer, this.#init).expectToBeVisible()
		)

		return this
	}

	expectNotToBeVisible() {
		this.setTask(
			testField(
				this.field.name, this.field.title
			)(this.#renderer, this.#init).expectNotToBeVisible()
		)

		return this
	}

	expectNotToBeInDocument() {
		this.setTask(
			testField(
				this.field.name, this.field.title
			)(this.#renderer, this.#init).expectNotToBeInDocument()
		)

		return this
	}

	expectToBeRequired() {
		this.setTask(
			testField(
				this.field.name, this.field.title
			)(this.#renderer, this.#init).expectToBeRequired()
		)

		return this
	}

	expectToHaveValue() {}
	expectToHaveCssClass() {}
}

export class FieldAsyncTest extends FieldTest {
	expectToBeVisible() {
		this.setTask(
			testFieldAsync(
				this.field.name,
				this.field.title
			)(
				this.getRenderer(),
				this.getInit(),
				this.getOptions()
			).expectToBeVisible()
		)

		return this
	}

	expectToBeRequired() {
		this.setTask(
			testFieldAsync(
				this.field.name,
				this.field.title
			)(
				this.getRenderer(),
				this.getInit(),
				this.getOptions()
			).expectToBeRequired()
		)

		return this
	}
}

export class TextFieldTest extends FieldTest {
	expectToHaveValue(value) {
		this.setTask(
			testTextField(
				this.field.name, this.field.title
			)(
				this.getRenderer(),
				this.getInit(),
				this.getOptions()
			).expectToHaveValue(value)
		)

		return this
	}
}

export class TextFieldAsyncTest extends FieldTest {
	expectToHaveValue(value) {
		this.setTask(
			testTextFieldAsync(
				this.field.name, this.field.title
			)(
				this.getRenderer(),
				this.getInit(),
				this.getOptions()
			).toHaveValue(value)
		)

		return this
	}
}

export class CheckBoxFieldTest extends FieldTest {
	expectToHaveValue(value) {
		this.setTask(
			testCheckBoxField(
				this.field.name, this.field.title
			)(
				this.getRenderer(),
				this.getInit(),
				this.getOptions()
			).expectToHaveValue(value)
		)

		return this
	}
}

export class CheckBoxFieldAsyncTest extends FieldTest {
	expectToHaveValue(value) {
		this.setTask(
			testCheckBoxFieldAsync(
				this.field.name, this.field.title
			)(
				this.getRenderer(),
				this.getInit(),
				this.getOptions()
			).toHaveValue(value)
		)

		return this
	}
}

export class SelectFieldTest extends FieldTest {
	expectToHaveValue(value) {
		this.setTask(
			testSelectField(
				this.field.name, this.field.title
			)(
				this.getRenderer(),
				this.getInit(),
				this.getOptions()
			).expectToHaveSelectedText(value)
		)

		return this
	}
}

export class SelectFieldAsyncTest extends FieldTest {
	expectToHaveValue(value) {
		this.setTask(
			testSelectFieldAsync(
				this.field.name, this.field.title
			)(
				this.getRenderer(),
				this.getInit(),
				this.getOptions()
			).expectToHaveSelectedText(value)
		)

		return this
	}
}

export class FieldTestFactory {
	#renderer
	#options

	constructor(renderer, options) {
		this.#renderer = renderer
		this.#options = options
	}

	static instance(fieldType, renderer, options) {
		switch (fieldType) {
			case FieldType.TEXT:
				return new TextFieldTestFactory(renderer, options)
			case FieldType.SELECT:
				return new SelectFieldTestFactory(renderer, options)
			case FieldType.CHECKBOX:
				return new CheckBoxFieldTestFactory(renderer, options)
		}
	}

	getRenderer() {
		return this.#renderer
	}

	getOptions() {
		return this.#options
	}

	create(name, title, init) {
		const field = new Field(name, title)
		return new FieldTest(field, this.getRenderer(), init, this.getOptions())
	}
}

export class FieldAsyncTestFactory extends FieldTestFactory {
	static instance(fieldType, renderer, options) {
		switch (fieldType) {
			case FieldType.TEXT:
				return new TextFieldAsyncTestFactory(renderer, options)
			case FieldType.SELECT:
				return new SelectFieldAsyncTestFactory(renderer, options)
			case FieldType.CHECKBOX:
				return new CheckBoxFieldAsyncTestFactory(renderer, options)
		}
	}

	create(name, title, init) {
		const field = new Field(name, title)
		return new FieldAsyncTest(field, this.getRenderer(), init, this.getOptions())
	}
}

export class TextFieldTestFactory extends FieldTestFactory {
	create(name, title, init) {
		const field = new TextField(name, title)
		return new TextFieldTest(field, this.getRenderer(), init, this.getOptions())
	}
}

export class TextFieldAsyncTestFactory extends FieldTestFactory {
	create(name, title, init) {
		const field = new TextField(name, title)
		return new TextFieldAsyncTest(field, this.getRenderer(), init, this.getOptions())
	}
}

export class CheckBoxFieldTestFactory extends FieldTestFactory {
	create(name, title, init) {
		const field = new CheckBoxField(name, title)
		return new CheckBoxFieldTest(field, this.getRenderer(), init, this.getOptions())
	}
}

export class CheckBoxFieldAsyncTestFactory extends FieldTestFactory {
	create(name, title, init) {
		const field = new CheckBoxField(name, title)
		return new CheckBoxFieldAsyncTest(field, this.getRenderer(), init, this.getOptions())
	}
}

export class SelectFieldTestFactory extends FieldTestFactory {
	create(name, title, init) {
		const field = new SelectField(name, title)
		return new SelectFieldTest(field, this.getRenderer(), init, this.getOptions())
	}
}

export class  SelectFieldAsyncTestFactory extends FieldTestFactory {
	create(name, title, init) {
		const field = new SelectField(name, title)
		return new SelectFieldAsyncTest(field, this.getRenderer(), init, this.getOptions())
	}
}