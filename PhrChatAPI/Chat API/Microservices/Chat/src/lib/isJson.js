"use strict";
module.exports = function isJson(str=null) {
	if( !(str) )	return false;

	try {
		return JSON.parse( str );
	} catch (e) {
		return false;
	}
}
