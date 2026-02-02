"use strict";
// only for debugging
// const debug					= require('../../../lib/debug');
const {Router}					= require('express');
const companyMigrateController	= require('../../../controllers/companyMigrate');
const ApiRouteUtilities 		= require('../../../lib/ApiRouteUtilities');

/* companyMigrate Router Class */
class routerCompanyMigrate extends ApiRouteUtilities{
	constructor(){
		// execute parent constructor
		super();

		// the router itself to export
		this.router = Router();

		this.router.post("/",this.postCompanyMigrate.bind(this));
		this.router.delete("/reset",this.resetCompanyMigrate.bind(this));
	}

	postCompanyMigrate(req,res,next){
		this.middlewareParams = arguments;
		let params = this.getRequestParams('body');
		let ctrl	= new companyMigrateController;

		return ctrl.createMigration(params)
		.then( this.postSuccessResponse.bind(this) )
		.catch( this.postErrorResponse.bind(this) );
	}

	resetCompanyMigrate(req,res,next){
		this.middlewareParams = arguments;
		let params = this.getRequestParams('body');
		let ctrl	= new companyMigrateController;

		return ctrl.resetMigration(params)
		.then( this.deleteSuccessResponse.bind(this) )
		.catch( this.deleteErrorResponse.bind(this) );
	}
}

let {router} = new routerCompanyMigrate;
module.exports = router;
