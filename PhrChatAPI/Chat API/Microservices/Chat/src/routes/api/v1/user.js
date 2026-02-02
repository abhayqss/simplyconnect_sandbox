"use strict";
// only for debugging
// const debug				= require('../../../lib/debug');
const {Router}				= require('express');
const UserController		= require('../../../controllers/User');
const JwtAuth 				= require('../../../middleware/jwtAuth');
const ApiRouteUtilities 	= require('../../../lib/ApiRouteUtilities');

/* User Router Class */
class routerUser extends ApiRouteUtilities{
	constructor(){
		// execute parent constructor
		super();

		// the router itself to export
		this.router = Router();

		this.router.get("/",JwtAuth,this.getUsers.bind(this));
		this.router.get("/:id",JwtAuth,this.getUser.bind(this));
		this.router.post("/",JwtAuth,this.postUser.bind(this));
		this.router.put("/:id",JwtAuth,this.putUser.bind(this));
		this.router.delete("/:id",JwtAuth,this.deleteUser.bind(this));

	}

	getUsers(req,res,next){
		this.middlewareParams = arguments;
		let params	= this.getRequestParams('decodedToken');
		let ctrl	= new UserController;
		return ctrl.getAll(params)
		.then( this.getSuccessResponse.bind(this) )
		.catch( this.getErrorResponse.bind(this) );
	}

	getUser(req,res,next){
		this.middlewareParams = arguments;
		let params	= this.getRequestParams('params,decodedToken');
		let ctrl	= new UserController;

		return ctrl.getOne(params)
		.then( this.getSuccessResponse.bind(this) )
		.catch( this.getErrorResponse.bind(this) );
	}

	postUser(req,res,next){
		this.middlewareParams = arguments;
		let params 	= this.getRequestParams('body');
		let ctrl	= new UserController;

		return ctrl.create(params)
		.then( this.postSuccessResponse.bind(this) )
		.catch( this.postErrorResponse.bind(this) );
	}

	putUser(req,res,next){
		this.middlewareParams = arguments;
		let params	= this.getRequestParams('body,decodedToken');
		let id		= req.params.id;
		let ctrl	= new UserController;

		return ctrl.edit(id,params)
		.then( this.putSuccessResponse.bind(this) )
		.catch( this.putErrorResponse.bind(this) );
	}

	deleteUser(req,res,next){
		this.middlewareParams = arguments;
		let params	= this.getRequestParams('body');
		let ctrl	= new UserController;

		return ctrl.delete(params)
		.then( this.deleteSuccessResponse.bind(this) )
		.catch( this.deleteErrorResponse.bind(this) );
	}
};

let route = new routerUser;
module.exports = route.router;
