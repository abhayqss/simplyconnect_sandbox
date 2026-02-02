"use strict";
// const ControllerModule = sys.getController("controllerFilename");
const Router			= require('express').Router

/* HelloWorld Router Class */
class routerHelloWorld{
	constructor(){
		// the router itself to export
		this.router = Router();

		this.router.get("/",this.getHelloWorld);
		// this.router.post("/uriStringHere",this.postHelloWorld);
		// this.router.put("/uriStringHere",this.putHelloWorld);
		// this.router.delete("/uriStringHere",this.deleteHelloWorld);
	};

	getHelloWorld(req,res,next){
		return res
		.json({message:'Hello World!'});
	};

	// postHelloWorld(req,res,next){
	// 	// body...
	// };
	// putHelloWorld(req,res,next){
	// 	// body...
	// };

	// deleteHelloWorld(req,res,next){
	// 	// body...
	// };
};

module.exports = new routerHelloWorld().router;
