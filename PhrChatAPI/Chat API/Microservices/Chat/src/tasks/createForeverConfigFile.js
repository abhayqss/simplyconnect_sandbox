"use strict";
const fs				= require('fs');
const path				= require('path');
const projectDir		= path.join(__dirname,'../../');
const destinyfilePath	= path.join(__dirname,'../../forever.json');
const {NODE_ENV=null,NAME=null} = process.env;
if( !(NODE_ENV) || !(NAME) ){
	var Env 	= require('dotenv').config({path: path.join(__dirname,'../../.env')});
}

const foreverConfig 	= JSON.stringify({
	"uid": process.env.NAME,
	"append": true,
	"watch": false,
	"script": "src/service.js",
	"sourceDir": projectDir,
	"logFile": path.join( projectDir , "foreverLogs/forever.log" ),
	"outFile": path.join( projectDir , "foreverLogs/out.log" ),
	"errFile": path.join( projectDir , "foreverLogs/err.log" ),
},null,2);

fs.writeFile( destinyfilePath ,foreverConfig,(err,some)=>{
	console.info('created file!', destinyfilePath);
});
