"use strict";
// only for debugging
// const debug 				= require('../../../lib/debug');
const {Router}				= require('express');
const HandsetController		= require('../../../controllers/Handset');
const JwtAuth 				= require('../../../middleware/jwtAuth');
const ApiRouteUtilities 	= require('../../../lib/ApiRouteUtilities');

/* Handset Router Class */
class routerHandset extends ApiRouteUtilities{
	constructor(){
		super();
		// the router itself to export
		this.router = Router();

		this.router.get("/",JwtAuth,this.getHandsets.bind(this));
		this.router.get("/:id",JwtAuth,this.getHandset.bind(this));
		// this.router.post("/",JwtAuth,this.postHandset.bind(this));
		this.router.post("/",this.postHandset.bind(this));
		this.router.put("/",JwtAuth,this.updateHandsetPnToken.bind(this));
		this.router.put("/:id",JwtAuth,this.putHandset.bind(this));
		this.router.delete("/:id",JwtAuth,this.deleteHandset.bind(this));
	}

	getHandsets(req,res,next){
		this.middlewareParams = arguments;
		let ctrl = new HandsetController;

		return ctrl.getAll()
		.then( this.getSuccessResponse.bind(this) )
		.catch( this.getErrorResponse.bind(this) );
	}

	getHandset(req,res,next){
		this.middlewareParams = arguments;
		let params = this.getRequestParams('params');
		let ctrl = new HandsetController;

		return ctrl.getOne(params)
		.then( this.getSuccessResponse.bind(this) )
		.catch( this.getErrorResponse.bind(this) );
	}

	postHandset(req,res,next){
		this.middlewareParams = arguments;
		let params = this.getRequestParams('body');
		
		let ctrl = new HandsetController;

		return ctrl.getHandsetByUUID(params)
		.then(()=>{
			return ctrl.updateHandsetByUUID(params)
			.then( this.postSuccessResponse.bind(this) )
			.catch( this.postErrorResponse.bind(this) );
		})
		.catch(()=>{
			return ctrl.create(params)
			.then( this.postSuccessResponse.bind(this) )
			.catch( this.postErrorResponse.bind(this) );
		})
	}

	updateHandsetPnToken(req,res,next){
		this.middlewareParams = arguments;
		let params	= this.getRequestParams('body');
		let ctrl 	= new HandsetController;

		return ctrl.updatePnToken(params)
		.then( this.putSuccessResponse.bind(this) )
		.catch( this.putErrorResponse.bind(this) );
	}

	putHandset(req,res,next){
		this.middlewareParams = arguments;
		let params 	= this.getRequestParams('body');
		let ctrl = new HandsetController;

		// QSS
		if(req.params.id && parseInt(req.params.id) != 0 ){
		
			let id		= req.params.id;
			return ctrl.edit(id,params)
			.then(this.putSuccessResponse.bind(this))
			.catch( this.putErrorResponse.bind(this) );
		}
		else{

			return ctrl.updateHandsetByUUID(params)
			.then( this.putSuccessResponse.bind(this) )
			.catch( this.putErrorResponse.bind(this) );
		}
	}

	deleteHandset(req,res,next){
		this.middlewareParams = arguments;
		let params = this.getRequestParams('body');
		let ctrl = new HandsetController;

		return ctrl.delete(params)
		.then( this.deleteSuccessResponse.bind(this) )
		.catch( this.deleteErrorResponse.bind(this) );
	}
}

let { router } = new routerHandset;
module.exports = router;
