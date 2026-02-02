"use strict";
// only for debugging
// const debug 			= require('../../../lib/debug');
const {Router}			= require('express');
const SessionController = require('../../../controllers/Session');
const JwtAuth 			= require('../../../middleware/jwtAuth');
const ApiRouteUtilities = require('../../../lib/ApiRouteUtilities');

/* Logout Router Class */
class routerLogout extends ApiRouteUtilities{
	constructor(){
		// execute parent constructor
		super();

		// the router itself to export
		this.router = Router();

		this.router.post("/",JwtAuth,this.postLogout.bind(this));
		this.router.delete("/",JwtAuth,this.postLogout.bind(this));
		this.router.post("/forceful",JwtAuth,this.forcefulLogout.bind(this));
		this.router.delete("/forceful",JwtAuth,this.forcefulLogout.bind(this));
	}

	postLogout(req,res,next){
		this.middlewareParams = arguments;
		let params = this.getRequestParams('decodedToken');
		let ctrl = new SessionController();

		return ctrl.delete(params)
		.then( this.deleteSuccessResponse.bind(this) )
		.catch( this.authErrorResponse.bind(this) );
	}

	forcefulLogout(req,res,next){
		this.middlewareParams = arguments;
		let params = this.getRequestParams('body');
		let ctrl = new SessionController;

		return ctrl.forcedRemoval(params)
		.then( this.deleteSuccessResponse.bind(this) )
		.catch( this.deleteErrorResponse.bind(this) );
	}
}

let {router} = new routerLogout;
module.exports = router;
