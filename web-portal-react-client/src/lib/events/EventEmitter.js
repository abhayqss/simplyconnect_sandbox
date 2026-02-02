import { EventEmitter as Emitter } from '@billjs/event-emitter'

export class EventEmitter {
	#emitter = new Emitter()

	on(type, handler) {
		return this.#emitter.on(type, handler)
	}

	once(type, handler) {
		return this.#emitter.once(type, handler)
	}

	off(type, handler) {
		return this.#emitter.off(type, handler)
	}

	offAll() {
		return this.#emitter.offAll()
	}

	fire(type, data) {
		return this.#emitter.fire(type, data)
	}

	has(type, handler) {
		return this.#emitter.has(type, handler)
	}

	getHandlers(type) {
		return this.#emitter.getHandlers(type)
	}

	createEvent(type, data, once) {
		return this.#emitter.createEvent(type, data, once)
	}
}

export default new EventEmitter()