import Store from './Store'

const AUTHENTICATED_USER = 'AUTHENTICATED_USER'

export class AuthUserStore extends Store {
  save (o) {
    return super.save(AUTHENTICATED_USER, o || null)
  }

  get () {
    return super.get(AUTHENTICATED_USER)
  }

  clear () {
    return super.clear(AUTHENTICATED_USER)
  }
}

export default new AuthUserStore()
