"use strict";
const os 		= require('os');
const path 		= require('path');
const fs 		= require('fs');
const parentDir	= path.join( __dirname , '../' );

class System{
	constructor(){
		if( process.cwd() != parentDir.slice( 0 , -1 ) ){
			// declare parent directory as basePath
			process.chdir( parentDir );
		}

		const baseDir = process.cwd();

		//project directories list
		this.dir = {
			base: 			baseDir,
			config: 		baseDir + "/config",
			controller: 	baseDir + "/controllers",
			lib: 			baseDir + "/lib",
			model: 			baseDir + "/models",
			route: 			baseDir + "/routes",
			view: 			baseDir + "/views",
			public: 		baseDir + "/../public",
		};
	}

	fileExists(pathTofile){
		return ( fs.existsSync( pathTofile ) );
	}

	getDir(dirPath=false,file=''){
		let RES;
		if( dirPath && dirPath in this.dir ){
			RES = path.join( this.dir[dirPath] , file );
		}

		return RES;
	}
}

module.exports = new System;
