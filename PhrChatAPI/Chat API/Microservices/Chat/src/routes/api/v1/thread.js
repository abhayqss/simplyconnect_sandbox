"use strict";
// only for debugging
// const debug 				= require('../../../lib/debug');
const {Router}				= require('express');
const ThreadController		= require('../../../controllers/Thread');
const JwtAuth 				= require('../../../middleware/jwtAuth');
const ApiRouteUtilities 	= require('../../../lib/ApiRouteUtilities');

/* Thread Router Class */
class routerThread extends ApiRouteUtilities{
	constructor(){
		// execute parent constructor
		super();

		// the router itself to export
		this.router = Router();

		this.router.get("/",JwtAuth,this.getThreads.bind(this));
		this.router.get("/:id",JwtAuth,this.getThread.bind(this));
		this.router.get("/:id/messages/:enabled",JwtAuth,this.threadGetMessages.bind(this));
		this.router.post("/",JwtAuth,this.postThread.bind(this));
		this.router.put("/:id",JwtAuth,this.putThread.bind(this));
		this.router.delete("/:id",JwtAuth,this.deleteThread.bind(this));
	}

	getThreads(req,res,next){
		this.middlewareParams = arguments;
		let params	= this.getRequestParams('decodedToken');
		let ctrl	= new ThreadController;

		return ctrl.getAll(params)
		.then( this.getSuccessResponse.bind(this) )
		.catch( this.getErrorResponse.bind(this) );
	}

	getThread(req,res,next){
		this.middlewareParams = arguments;
		let params	= this.getRequestParams('params,decodedToken');
		let ctrl	= new ThreadController;

		return ctrl.getOne(params)
		.then( this.getSuccessResponse.bind(this) )
		.catch( this.getErrorResponse.bind(this) );
	}

	threadGetMessages(req,res,next){
		this.middlewareParams = arguments;
		let params	= this.getRequestParams('params,decodedToken');
		let ctrl	= new ThreadController;

		return ctrl.getMessages(params)
		.then( this.getSuccessResponse.bind(this) )
		.catch( this.getErrorResponse.bind(this) );
	}

	postThread(req,res,next){
		this.middlewareParams = arguments;
		let params 	= this.getRequestParams('body,decodedToken');
		let ctrl	= new ThreadController;

		return ctrl.create(params)
		.then( this.postSuccessResponse.bind(this) )
		.catch( this.postErrorResponse.bind(this) );
	}

	putThread(req,res,next){
		this.middlewareParams = arguments;
		let params	= this.getRequestParams('body,decodedToken');
		let id		= req.params.id;
		let ctrl	= new ThreadController;

		return ctrl.edit(id,params)
		.then( this.putSuccessResponse.bind(this) )
		.catch( this.putErrorResponse.bind(this) );
	}

	deleteThread(req,res,next){
		this.middlewareParams = arguments;
		let params	= this.getRequestParams('params,decodedToken');
		let ctrl	= new ThreadController;

		return ctrl.delete(params)
		.then( this.deleteSuccessResponse.bind(this) )
		.catch( this.deleteErrorResponse.bind(this) );
	}
}

let route = new routerThread;
module.exports = route.router;
