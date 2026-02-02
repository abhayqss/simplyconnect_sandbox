import mirror from 'key-mirror'

export const STEP = {
    SELECT_TYPE: 0,
    SELECT_USER: 1,
}

export const PARTICIPANT_TYPE = mirror({
    CLIENT: null,
    CONTACT: null
})
