module.exports = {
	Defaults: {
		limit: 100,
		offset: 0,
	},
	Responses : {
		auth: {
			succ: {
				status : 200,
				success: true,
				message: 'Authentication successful',
			},
			err: {
				status : 401,
				success: false,
				message: 'Authentication failed',
			},
		},
		get: {
			succ:{
				status : 200,
				success: true,
				message: 'Object found',
				data: null
			},
			err: {
				status : 404,
				success: false,
				message: 'Object Not found',
			},
		},
		post: {
			succ:{
				status : 201,
				success: true,
				message: 'Object created',
				data: null
			},
			err: {
				status : 500,
				success: false,
				message: 'Operation failed',
				error: null,
			},
		},
		put: {
			succ:{
				status : 200,
				success: true,
				message: 'Object updated',
				data: null
			},
			err: {
				status : 500,
				success: false,
				message: 'Operation failed',
				error: null,
			},
		},
		delete: {
			succ: {
				status : 200,
				success: true,
				message: 'Object deleted',
			},
			err: {
				status : 500,
				success: false,
				message: 'Operation failed',
				error: null,
			},
		},
		system_error: {
			status : 500,
			success: false,
			message: 'Operation failed',
		},
	},
};
