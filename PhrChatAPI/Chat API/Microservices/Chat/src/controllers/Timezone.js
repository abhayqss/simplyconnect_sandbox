"use strict";
// only for debugging
// const debug		= require('../lib/debug');
const Models	= require('../models');

/* Timezone Controller Class */
module.exports = class controllerTimezone{
	constructor(){
	}

	getOne({id}){
		return new Promise((Resolve,Reject)=>{

			return Models.timezone.findById(id)
			.then((Timezone)=>{
				if (!Timezone) return Reject('Non-existent Timezone');
				return Timezone;
			})
			.then( data => Resolve(data) )
			.catch( Err => Reject(Err) );
		});
	}

	getAll(params){
		return new Promise((Resolve,Reject)=>{

			let Wh = {
				limit: params.limit,
				offset: params.offset,
				order: [['utc_offset','ASC']]
			};

			return Models.timezone.findAndCountAll(Wh)
			.then((Timezone)=>{
				if (!Timezone) return Reject('Non-existent Timezone');

				return Timezone;
			})
			.then( data => Resolve(data) )
			.catch( Err => Reject(Err) );
		});
	}
}
