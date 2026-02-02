"use strict";
const Moment		= require('moment');
const Models		= require('../models');
const config		= require('../config/messageCleanup');

module.exports = function(){
	console.info('starting old Message cleanup CronJob');

	let antiquity = Moment();
	if( config.months && config.months > 0 )	antiquity.subtract(config.months,'months');
	if( config.days && config.days > 0 )		antiquity.subtract(config.days,'days');
	if( config.hours && config.hours > 0 )		antiquity.subtract(config.hours,'hours');
	if( config.minutes && config.minutes > 0 )	antiquity.subtract(config.minutes,'minutes');

	let wh = {
		where: {
			createdAt: {
				$lte: antiquity.toDate()
			}
		},
	};

	console.info(`Deleting Messages older than ${antiquity}`);
	return Models.thread_message.destroy(wh)
	.then((deletedMsgs)=>{

		let log = ( deletedMsgs > 0 ) ? `${deletedMsgs} messages successfully deleted!` : `There were no messages to delete`;
		return console.info( log );
	})
	.catch((Err)=>{
		return console.error(Err);
	});
}
