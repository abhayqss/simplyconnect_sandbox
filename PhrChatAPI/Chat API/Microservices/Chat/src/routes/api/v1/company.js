"use strict";
// only for debugging
// const debug					= require('../../../lib/debug');
const {Router}					= require('express');
const companyController			= require('../../../controllers/Company');
const JwtAuth 					= require('../../../middleware/jwtAuth');
const ApiRouteUtilities 		= require('../../../lib/ApiRouteUtilities');

/* Company Router Class */
class routerCompany extends ApiRouteUtilities{
	constructor(){
		// execute parent constructor
		super();

		// the router itself to export
		this.router = Router();

		this.router.get("/",JwtAuth,this.getCompanies.bind(this));
		this.router.get("/:id",JwtAuth,this.getCompany.bind(this));
		this.router.post("/",JwtAuth,this.postCompany.bind(this));
		this.router.put("/:id",JwtAuth,this.putCompany.bind(this));
		this.router.delete("/:id",JwtAuth,this.deleteCompany.bind(this));
	}

	getCompanies(req,res,next){
		this.middlewareParams = arguments;
		let ctrl	= new companyController;

		return ctrl.getAll()
		.then( this.getSuccessResponse.bind(this) )
		.catch( this.getErrorResponse.bind(this) );
	}

	getCompany(req,res,next){
		this.middlewareParams = arguments;
		let params = this.getRequestParams('params,decodedToken');
		let ctrl	= new companyController;

		return ctrl.getOne(params)
		.then( this.getSuccessResponse.bind(this) )
		.catch( this.getErrorResponse.bind(this) );
	}

	postCompany(req,res,next){
		this.middlewareParams = arguments;
		let params = this.getRequestParams('body,decodedToken');
		let ctrl	= new companyController;

		return ctrl.create(params)
		.then( this.postSuccessResponse.bind(this) )
		.catch( this.postErrorResponse.bind(this) );
	}

	putCompany(req,res,next){
		this.middlewareParams = arguments;
		let params	= this.getRequestParams('body,decodedToken');
		let id		= req.params.id;
		let ctrl	= new companyController;

		return ctrl.edit(id,params)
		.then( this.putSuccessResponse.bind(this) )
		.catch( this.putErrorResponse.bind(this) );
	}

	deleteCompany(req,res,next){
		this.middlewareParams = arguments;
		let params	= this.getRequestParams('params,decodedToken');
		let ctrl = new companyController;

		return ctrl.delete(params)
		.then( this.deleteSuccessResponse.bind(this) )
		.catch( this.deleteErrorResponse.bind(this) );
	}
}

let route = new routerCompany;
module.exports = route.router;
