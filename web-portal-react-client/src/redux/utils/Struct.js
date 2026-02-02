const { Record } = require('immutable')

export default function Struct(data) {
    return Record(data)()
}
