#!/usr/bin/env node
"use strict";

/**
* declare parent directory as basePath
*/
const path				= require('path');
process.chdir( path.join( __dirname , '../' ) );

/**
* Load Module dependencies.
*/
const os 					= require('os');
const CronJob				= require('cron').CronJob;
const SocketIO				= require('socket.io');
const Env 					= require('dotenv').config({path: path.join(__dirname,'../.env')});
const debug 				= require('debug')(`${process.env.NAME}:server`);
const ioHandler 			= require('./lib/ioHandler');
const delOldChatMsgs		= require('./lib/delOldChatMsgs');
const port					= normalizePort(process.env.PORT);
const App					= require('./application');
const fs 					= require('fs');

/**
* Get port from environment and store in Express.
*/
App.set('port', port);

/**
* Create HTTPS server.
*/
const https					= require('https');
const httpsOptions = {
	key: fs.readFileSync(__dirname + '/config/private.key'),
	cert: fs.readFileSync(__dirname + '/config/primary.crt'),
	requestCert: false
}

const server1 = https.createServer(httpsOptions, App);

/**
* Create HTTP server.
*/
 const http					= require('http');
 const server2 = http.createServer(App);


/**
* Store an Initialized socket Handler in global scope.
*/
console.log('\n\tstarting RT server\n');
let ioServer		= SocketIO( server1 );
global.ioHandler	= new ioHandler( ioServer );

/**
* Listen on provided port, on all network interfaces.
*/
server1.listen(port);
server1.on('error', onError);
server1.on('listening', onListening);

server2.listen(9899);
server2.on('error', onError);
server2.on('listening', onListening);

/**
* Normalize a port into a number, string, or false.
*/
function normalizePort(val) {
	const port = parseInt(val, 10);

	if (isNaN(port)) {
		// named pipe
		return val;
	}

	if (port >= 0) {
		// port number
		return port;
	}

	return false;
}

/**
* Event listener for HTTP server "error" event.
*/
function onError(error) {
	if (error.syscall !== 'listen') {
		throw error;
	}

	const bind = typeof port === 'string'
	? 'Pipe ' + port
	: 'Port ' + port;

	// handle specific listen errors with friendly messages
	switch (error.code) {
		case 'EACCES':
		console.error(bind + ' requires elevated privileges');
		process.exit(1);
		break;
		case 'EADDRINUSE':
		console.error(bind + ' is already in use');
		process.exit(1);
		break;
		default:
		throw error;
	}
}

/**
* Event listener for HTTP server "listening" event.
*/
function onListening() {
	const addr = server1.address();
	let bind = (typeof addr === 'string') ? `pipe ${addr}` : `port ${addr.port}`;
	const msg = `${process.env.NAME} is Listening on port: ${bind}`;

	debug(msg);
	console.log("\n"+msg);
	console.log('\tListening in the following local addresses:\n');

	// inform about localhost ip addresses
	let interfaces = os.networkInterfaces();
	for (let k in interfaces) {
		for (let k2 in interfaces[k]) {
			let address = interfaces[k][k2];

			if (address.family === 'IPv4' && !address.internal) {
				console.log(`http://${address.address}:${port}`);
			}
		}
	}
}

/**
* Messages Cleanup CronJob
* Runs Everyday at 11:59 PM
* for config read http://crontab.org/
*/
try {
	new CronJob(
		'00 59 23 * * 1-6',
		delOldChatMsgs,
		()=>{ return console.warn('Messages Cleanup CronJob was interrupted'); },
		true,
		'America/Los_Angeles'
	);
} catch (error) {
	console.error("cron pattern not valid");
}
