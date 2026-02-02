"use strict";
// only for debugging
// const debug				= require('../../../lib/debug');
const {Router}				= require('express');
const TimezoneController	= require('../../../controllers/Timezone');
const JwtAuth 				= require('../../../middleware/jwtAuth');
const ApiRouteUtilities 	= require('../../../lib/ApiRouteUtilities');

/* Timezone Router Class */
class routerTimezone extends ApiRouteUtilities{
	constructor(){
		// execute parent constructor
		super();

		// the router itself to export
		this.router = Router();

		this.router.get("/",JwtAuth,this.getTimeZones.bind(this));
		this.router.get("/:id",JwtAuth,this.getTimezone.bind(this));
	}

	getTimeZones(req,res,next){
		this.middlewareParams = arguments;
		let params	= this.getRequestParams('query');
		let ctrl	= new TimezoneController;

		return ctrl.getAll(params)
		.then( this.getSuccessResponse.bind(this) )
		.catch( this.getErrorResponse.bind(this) );
	}

	getTimezone(req,res,next){
		this.middlewareParams = arguments;
		let params	= this.getRequestParams('params,decodedToken');
		let ctrl	= new TimezoneController;

		return ctrl.getOne(params)
		.then( this.getSuccessResponse.bind(this) )
		.catch( this.getErrorResponse.bind(this) );
	}
}

let route = new routerTimezone;
module.exports = route.router;
