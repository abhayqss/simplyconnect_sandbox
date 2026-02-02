"use strict";
// only for debugging
// const {dump,die,dd} 			= require('../../../lib/debug');
const {Router}					= require('express');
const MessageController			= require('../../../controllers/Message');
const JwtAuth 					= require('../../../middleware/jwtAuth');
const ApiRouteUtilities 		= require('../../../lib/ApiRouteUtilities');

/* Message Router Class */
class routerMessage extends ApiRouteUtilities{
	constructor(){
		// execute parent constructor
		super();

		// the router itself to export
		this.router = Router();

		this.router.get("/",JwtAuth,this.getMessages.bind(this));
		this.router.get("/:id",JwtAuth,this.getMessage.bind(this));
		this.router.post("/",JwtAuth,this.postMessage.bind(this));
		this.router.put("/:id",JwtAuth,this.putMessage.bind(this));
		this.router.delete("/:id",JwtAuth,this.deleteMessage.bind(this));
	}

	getMessages(req,res,next){
		this.middlewareParams = arguments;
		let params = this.getRequestParams('decodedToken');
		let ctrl	= new MessageController;

		return ctrl.getAll(params)
		.then( this.getSuccessResponse.bind(this) )
		.catch( this.getErrorResponse.bind(this) );
	}

	getMessage(req,res,next){
		this.middlewareParams = arguments;
		let params = this.getRequestParams('params,decodedToken');
		let ctrl	= new MessageController;

		return ctrl.getOne(params)
		.then( this.getSuccessResponse.bind(this) )
		.catch( this.getErrorResponse.bind(this) );
	}

	postMessage(req,res,next){

		this.middlewareParams = arguments;
		let params = this.getRequestParams('body,decodedToken');
		let ctrl	= new MessageController;
		
		if(req.headers['phrauthtoken']){
			params['phrAuthToken'] = req.headers['phrauthtoken'];
		}		
		
		return ctrl.create(params)
		.then( this.postSuccessResponse.bind(this) )
		.catch( this.postErrorResponse.bind(this) );
	}

	putMessage(req,res,next){
		this.middlewareParams = arguments;
		let params	= this.getRequestParams('body,decodedToken');
		let id		= req.params.id;
		let ctrl	= new MessageController;

		return ctrl.edit(id,params)
		.then( this.putSuccessResponse.bind(this) )
		.catch( this.putErrorResponse.bind(this) );
	}

	deleteMessage(req,res,next){
		this.middlewareParams = arguments;
		let params	= this.getRequestParams('params,decodedToken');
		let ctrl = new MessageController;

		return ctrl.delete(params)
		.then( this.deleteSuccessResponse.bind(this) )
		.catch( this.deleteErrorResponse.bind(this) );
	}
}

let route = new routerMessage;
module.exports = route.router;
