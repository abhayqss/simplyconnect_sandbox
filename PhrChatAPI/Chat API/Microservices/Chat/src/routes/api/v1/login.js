"use strict";
// only for debugging
// const debug			= require('../../../lib/debug');
const {Router}			= require('express');
const SessionController = require('../../../controllers/Session');
const ApiRouteUtilities = require('../../../lib/ApiRouteUtilities');
const UserController		= require('../../../controllers/User');
// const ThreadController		= require('../../../controllers/Thread');

/* Login Router Class */
class routerLogin extends ApiRouteUtilities{
	constructor(){
		// execute parent constructor
		super();

		// the router itself to export
		this.router = Router();

		this.router.post("/",this.postLogin.bind(this));
	}

	postLogin(req,res,next){		

		this.middlewareParams = arguments;

		let params = this.getRequestParams('body,clientIp');

		let ctrl = new SessionController();

		return ctrl.create(params)
		.then( this.authSuccessResponse.bind(this) )
		.catch( this.authErrorResponse.bind(this) );
	}
}

let route = new routerLogin;
module.exports = route.router;
