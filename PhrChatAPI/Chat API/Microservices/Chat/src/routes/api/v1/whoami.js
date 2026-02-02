"use strict";
// only for debugging
// const debug 					= require('../../../lib/debug');
const {Router}					= require('express');
const WhoamiController			= require('../../../controllers/Whoami');
const JwtAuth 					= require('../../../middleware/jwtAuth');
const ApiRouteUtilities 		= require('../../../lib/ApiRouteUtilities');

/* Whoami Router Class */
class routerWhoami extends ApiRouteUtilities{
	constructor(){
		// execute parent constructor
		super();

		// the router itself to export
		this.router = Router();

		this.router.get("/",JwtAuth,this.getWhoami.bind(this));
		this.router.post("/",JwtAuth,this.getWhoami.bind(this));
		this.router.get("/threads",JwtAuth,this.whoamiThreads.bind(this));
		this.router.post("/threads",JwtAuth,this.whoamiThreads.bind(this));
	}

	getWhoami(req,res,next){
		this.middlewareParams = arguments;
		let params	= this.getRequestParams('decodedToken');
		let ctrl	= new WhoamiController;

		return ctrl.get(params)
		.then( this.getSuccessResponse.bind(this) )
		.catch( this.getErrorResponse.bind(this) );
	}

	whoamiThreads(req,res,next){

		this.middlewareParams = arguments;
		let params	= this.getRequestParams('decodedToken');

		// pass user id from parameters
		params['user_id'] = req.body.user_id;

		let ctrl	= new WhoamiController;

		return ctrl.getThreads(params)
		.then((Res)=>{res.status( 200 ).json(Res)})
		.catch( this.getErrorResponse.bind(this) );
	}
}

let route = new routerWhoami;
module.exports = route.router;
