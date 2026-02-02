"use strict";
const Express				= require('express');
const logger				= require('morgan');
const bodyParser			= require('body-parser');
const cors					= require('cors');
const requestIp				= require('request-ip');
const moment				= require('moment');
const sys 					= require('./lib/System');
const acceptUrlencoded		= require('./middleware/acceptUrlencoded');
const error404				= require('./middleware/error404');
const fs 					= require('fs');
const path				= require('path');

/**
* First line on Console
*/
console.log(`
\\\\***************************************************************************************//
		Starting ${process.env.NAME}:server in "${process.env.NODE_ENV}" mode!
		date: ${moment().format('LLLL')}
\\\\***************************************************************************************//
`);

// Create and config a new ExpressJs web Application
class Application{
	constructor() {
		this.express = Express();

		this
		.middleware()
		// .viewsConfig()
		// .exposePubicPath()
		.exposeRoutes()
		.catch404();
	}

	get getRoutes(){
		const routesConfig = require('./config/routes');
		let res = {};

		for(let key in routesConfig ){
			let routerPath		= sys.getDir('route',routesConfig[key]);
			let routerExists	= sys.fileExists(`${routerPath}.js`);
			let router			= routerExists ? require( routerPath ) : false;
			let resKey			= '/';
			resKey += ( key == 'default' ) ? '' : key;

			res[ resKey ] = {
				name: key,
				path: routesConfig[key],
				router: router,
			};
		}

		return res;
	}

	middleware(){
		// this.express.use( favicon( sys.getDir('public','favicon.ico') );
		var logFile = fs.createWriteStream(__dirname + '/../foreverLogs/morgan.log',{flags: 'a'});
		(process.env.NODE_ENV.toLowerCase() == "production") ? 
		this.express.use( logger('combined', {stream: logFile })) : 
		this.express.use( logger('dev'));
		this.express.use( bodyParser.json() );
		this.express.use( bodyParser.urlencoded({ limit:'1024kb' , extended: true }) );
		this.express.use( cors() );
		this.express.use( requestIp.mw() );
		this.express.use( acceptUrlencoded );

		return this;
	}

	viewsConfig(){
		// view engine setup
		this.express.set('views', sys.dir.view );
		this.express.set('view engine', 'pug');

		return this;
	}

	exposePubicPath(){
		this.express.use( Express.static( sys.dir.public ) );

		return this;
	}

	exposeRoutes(){
		const loadedRouters = this.getRoutes;
		for( let key in loadedRouters ){
			if( process.env.NODE_ENV != 'production' ){
				if( loadedRouters[key].router )
					console.log(`exposing route: "${key}"${'\t'}from: "${loadedRouters[key].path}" router file`);
			}

			if( !loadedRouters[key].router ){
				console.error(`WARNING: The router named: "${loadedRouters[key].name}" does not exist in the path "${loadedRouters[key].path}" please check the file "/config/routes.js"`);
			} else {
				this.express.use( key , loadedRouters[key].router );
			}
		}

		return this;
	}

	catch404(){
		// catch 404 and handle it
		this.express.use( error404 );

		return this;
	}
}

let {express} = new Application;
module.exports = express;
