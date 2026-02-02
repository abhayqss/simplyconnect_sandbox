import Store from './Store'

const PRIMARY_ORGANIZATION = 'PRIMARY_ORGANIZATION'

export class PrimaryOrganizationStore extends Store {
  save (o) {
    return super.save(PRIMARY_ORGANIZATION, o)
  }

  get () {
    return super.get(PRIMARY_ORGANIZATION)
  }

  clear () {
    return super.clear(PRIMARY_ORGANIZATION)
  }
}

export default new PrimaryOrganizationStore()