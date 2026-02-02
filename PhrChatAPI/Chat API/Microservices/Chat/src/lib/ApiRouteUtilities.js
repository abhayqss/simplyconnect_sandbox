"use strict";
// only for debugging
// const debug 	= require('./debug');
const Constants = require("./Constants");

module.exports = class ApiRouteUtilities{
	constructor(){
		this._middlewareParams = {
			req		: null,
			res		: null,
			next	: null,
		};
		this._limit = Constants.Defaults.limit;
		this._offset = Constants.Defaults.offset;
	}

	set middlewareParams([req,res,next]){
		this._middlewareParams.req	= req;
		this._middlewareParams.res	= res;
		this._middlewareParams.next	= next;

		if( req.query && req.query.limit ) this.limit = req.query.limit;
		if( req.query && req.query.offset ) this.offset = req.query.offset;
		if( req.body && req.body.limit ) this.limit = req.body.limit;
		if( req.body && req.body.offset ) this.offset = req.body.offset;
	}

	set limit(limit){
		if( typeof limit === 'number' || ( typeof limit === 'string' && /^\d+$/.test( limit ) ) ){
			// parse int
			limit = parseInt(limit);
			// allow MAX limit to defined in constants file
			this._limit = ( limit > 0 && limit < Constants.Defaults.limit ) ? limit : Constants.Defaults.limit;
		}
	}

	set offset(offset){
		if( typeof offset === 'number' || ( typeof offset === 'string' && /^\d+$/.test( offset ) ) ){
			// parse int
			offset = parseInt(offset);
			// offset must be Major than 0
			this._offset = ( offset > 0 ) ? offset : Constants.Defaults.offset;
		}
	}

	get expressResponse(){
		if( (this._middlewareParams.res) && typeof this._middlewareParams.res === 'object' ){
			return this._middlewareParams.res;
		}

		throw 'you forgot to provide this.middlewareParams with an express Middleware parameters Response e.g.	"this.middlewareParams = arguments;"';
	}

	get limit(){
		return parseInt( this._limit );
	}

	get offset(){
		return parseInt( this._offset );
	}

	getRequestParams(paramString){
		let params = [{},{limit:this.limit,offset:this.offset}];
		let reqParamsArr = paramString
			.split(',')
			.map((p) =>{
				if( p in this._middlewareParams.req ){
					return  ( typeof this._middlewareParams.req[p] == 'object' ) ? this._middlewareParams.req[p] : { [p] : this._middlewareParams.req[p] };
				}
			})
		params = params.concat( reqParamsArr );

		return Object.assign.apply(this,params);
	}

	getSuccessResponse(params){
		let Res = Constants.Responses['get'].succ;

		let {rows=false,count=-1} = params;
		if( (rows) && count >= 0 ){
			Res.data	= rows;
			Res.limit	= this.limit;
			Res.offset	= this.offset;
			Res.count	= count;

			// reset L&O to it's defaults
			this.limit	= Constants.Defaults.limit;
			this.offset	= Constants.Defaults.offset;
		} else {
			Res.data = params;
		}

		return this.expressResponse.status( Res.status ).json( Res );
	}

	getErrorResponse(params){
		let Res = Constants.Responses['get'].err;
		let msg;
		let error;
		if( typeof params === 'string' ){
			error = msg = params;
		} else if( typeof  params === 'object' ){
			msg =  params.message;
			error = params.errors
		};

		Res.message	= msg || Res.message;
		Res.error	= error || Res.error;

		return this.expressResponse.status( Res.status ).json( Res );
	}

	postSuccessResponse(params){
		let Res = Constants.Responses['post'].succ;

		Res.data	= params;

		return this.expressResponse.status( Res.status ).json( Res );
	}

	postErrorResponse(params){
		let Res = Constants.Responses['post'].err;
		let msg;
		let error;
		if( typeof params === 'string' ){
			error = msg = params;
		} else if( typeof  params === 'object' ){
			msg =  params.message;
			error = params.errors
		};

		Res.message	= msg || Res.message;
		Res.error	= error || Res.error;
		if(params.data) Res.data = params.data;

		return this.expressResponse.status( Res.status ).json( Res );
	}


	putSuccessResponse(params){
		let Res = Constants.Responses['put'].succ;

		Res.data	= params;

		return this.expressResponse.status( Res.status ).json( Res );
	}

	putErrorResponse(params){
		let Res = Constants.Responses['put'].err;
		let msg;
		let error;
		if( typeof params === 'string' ){
			error = msg = params;
		} else if( typeof  params === 'object' ){
			msg =  params.message;
			error = params.errors
		};

		Res.message	= msg || Res.message;
		Res.error	= error || Res.error;

		return this.expressResponse.status( Res.status ).json( Res );
	}

	deleteSuccessResponse(params){
		let Res = Constants.Responses['delete'].succ;

		return this.expressResponse.status( Res.status ).json( Res );
	}

	deleteErrorResponse(params){
		let Res = Constants.Responses['delete'].err;
		let msg;
		let error;
		if( typeof params === 'string' ){
			error = msg = params;
		} else if( typeof  params === 'object' ){
			msg =  params.message;
			error = params.errors
		};

		Res.message	= msg || Res.message;
		Res.error	= error || Res.error;

		return this.expressResponse.status( Res.status ).json( Res );
	}

	authSuccessResponse(params){
		let Res		= Constants.Responses['auth'].succ;

		Res.data = params;

		return this.expressResponse.status( Res.status ).json( Res );
	}

	authErrorResponse(params){
		let Res = Constants.Responses['auth'].err;
		let msg;
		let error;
		if( typeof params === 'string' ){
			error = msg = params;
		} else if( typeof  params === 'object' ){
			msg =  params.message;
			error = params.errors
		};

		Res.message	= msg || Res.message;
		Res.error	= error || Res.error;

		return this.expressResponse.status( Res.status ).json( Res );
	}
}
