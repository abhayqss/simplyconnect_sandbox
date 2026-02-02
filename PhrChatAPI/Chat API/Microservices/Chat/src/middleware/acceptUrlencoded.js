// API access allowed only to content-type': 'application/x-www-form-urlencoded except for GET
module.exports = (req,res,next)=>{
	if(req.headers['content-type'].includes(';')){
		req.headers['content-type'] = req.headers['content-type'].split(';')[0];
	}
	if(  req.method == 'GET' || (req.headers['content-type'] && req.headers['content-type'] === 'application/x-www-form-urlencoded') ){
		return next();
	} else {
		let data = {status:406, success: false, message: `Requests header must contain: 'content-type': 'application/x-www-form-urlencoded'`};
		return res
		.status(data.status)
		.json(data);
	}
}
