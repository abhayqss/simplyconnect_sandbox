"use strict";
// only for debugging
// const debug = require('../lib/debug');
const ApiRouteUtilities = require('../lib/ApiRouteUtilities');
const SessionController = require('../controllers/Session');

module.exports = function JwtAuth(req,res,next){	
	
	let util = new ApiRouteUtilities;
	util.middlewareParams = arguments;
	let ctrl = new SessionController();
	let token = req.body.token || req.query.token || req.headers['authorization'] || req.headers['JWT'] || req.headers['jwt'];

	return ctrl.validate(token)
	.then((data)=>{
		
		req.decodedToken = data.decodedToken;		
		return next();
	})
	.catch((Err)=>{
		
		return util.authErrorResponse(Err);
	});
}
