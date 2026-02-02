import { object, string } from 'yup'

const Scheme = object().shape({
	value: string().nullable().required()
})

export default Scheme
