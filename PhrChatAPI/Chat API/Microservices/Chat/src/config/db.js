"use strict";
const {NODE_ENV=null,NAME=null} = process.env;
if( !(NODE_ENV) || !(NAME) ){
	var path	= require('path');
	var Env 	= require('dotenv').config({path: path.join(__dirname,'../../.env')});
}

const dbConfigs = {
	"development": {
		"username": "root",
		"password": null,
		"database": "database_development",
		"host": "127.0.0.1",
		"dialect": "mysql",
		"port": 3306
	},
	"production": {
		"username": "sa",
		"password": "qp?n-ytu?",
		"database": "exchange_demo",
		"host": "10.240.1.120",
		"dialect": "mssql",
		"port": 1433
	}
};

module.exports = dbConfigs[ process.env.NODE_ENV ];
