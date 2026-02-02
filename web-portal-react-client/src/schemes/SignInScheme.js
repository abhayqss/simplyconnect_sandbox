import { Shape, stringMax } from './types'

const SignInScheme = Shape({
	username: stringMax(256).nullable().required(),
	password: stringMax(128).nullable().required(),
})

export default SignInScheme
