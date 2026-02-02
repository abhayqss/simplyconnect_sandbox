"use strict";

/*
 * ---------------------------------------------------
 * Config Routes file
 * ---------------------------------------------------
 *	-	This file lets you re-map URI requests to specific router files.
 * 	-	As long this file returns a plan object this will work well.
 *
 *  ---------------------------------------------------
 *	Warning:
 * ---------------------------------------------------
 *	-	Dont Touch Reserved Routes Object Keys just values.
 *
 *
 * ---------------------------------------------------
 *	Notice:
 * ---------------------------------------------------
 *	-	Reserved route "default" will be exposed as "/".
 *
 *	- 	If loader can't find a route file in Routes Path it will be Skipped
 *		and you will be noticed in console  about that.
 */
module.exports = {
	/* ------- RESERVED ROUTES -------- */
	// "default" : 		"/HelloWorld",

	/* -------  ROUTES -------- */
	"api/company": 			"/api/v1/company",
	"api/handset": 			"/api/v1/handset",
	"api/login": 			"/api/v1/login",
	"api/logout": 			"/api/v1/logout",
	"api/message": 			"/api/v1/message",
	"api/thread": 			"/api/v1/thread",
	"api/timezone": 		"/api/v1/timezone",
	"api/user": 			"/api/v1/user",
	"api/whoami": 			"/api/v1/whoami",
	"api/company_migrate": 	"/api/v1/company_migrate"
};
