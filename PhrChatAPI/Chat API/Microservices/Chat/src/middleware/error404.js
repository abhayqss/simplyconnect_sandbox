const {Responses}	= require('../lib/Constants');
const errorDto			= Responses.get.err;

module.exports = (req, res, next)=>{
	const err = new Error('Not Found');
	err.status = 404;
	// set locals, only providing error in development
	res.locals.message = err.message;
	res.locals.error = req.app.get('env') === 'development' ? err : {};

	errorDto.message = `Not-existent Endpoint '${req.url}' for Method: '${req.method}'`;

	// render the error page
	return res.status( errorDto.status ).json( errorDto );
};
