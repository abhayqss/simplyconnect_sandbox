"use strict";

/* Index Router Class */
const indexRouter = (function(){
	function indexRouter(){
		// Router to Expose
		this.router = sys.express.Router();

		return this.constructor();
	}

	indexRouter.prototype.constructor = function(){
		this.router.get('/',this.rootGet);
	};

	/* GET home page. */
	indexRouter.prototype.rootGet = function(req,res,next){
		let data = {
			title: sys.env.name,
		};

		return res.render( 'index' , data );
	};

	return indexRouter;
})();

module.exports = new indexRouter().router;
