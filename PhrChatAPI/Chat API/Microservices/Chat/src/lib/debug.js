"use strict";

class debug{
	dump(){
		console.log("\n//*------- start Data dumping...\n");
		for ( var arg in arguments ){
			// Notify about which arg are dumping
			console.info('\n\n\t...for argument:{'+arg+"}\n");
			//print type
			console.log( 'Type: ', typeof arguments[arg] );
			// print length if exists
			if( arguments[arg] && Array.isArray(arguments[arg]) ){
				console.log( 'Length: ', arguments[arg].length );
			}
			// print Value
			if( typeof arguments[arg] === "string" || /^\d+$/.test( arguments[arg] ) ){
				console.log( 'Value: ', arguments[ arg ] );
			} else {
				console.log( 'Value: ');
				console.dir( arguments[ arg ] );
			}
		}
		console.log("\n\t\tData dump done! -------*//\n");
	}

	die(){
		console.trace();
		console.info("\n\n\nnow Process will die\n\n");
		process.exit();
	}

	dd(){
		this.dump.apply( this, arguments );
		this.die();
	}
}

module.exports = new debug();
