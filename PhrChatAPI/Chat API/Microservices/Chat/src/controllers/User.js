"use strict";
// only for debugging
// const debug		= require('../lib/debug');
const Models	= require('../models');

/* User Controller Class */
module.exports = class controllerUser{
	constructor(){
	}

	create(params){
		return new Promise((Resolve,Reject)=>{

			return Models.user.create(params)
			.then( data => Resolve(data) )
			.catch( Err => Reject(Err) );
		});
	}

	getOne({id}){
		return new Promise((Resolve,Reject)=>{

			return Models.user.findById(id)
			.then((User)=>{
				if (!User) return Reject('Non-existent User');
				return User;
			})
			.then( data => Resolve(data) )
			.catch( Err => Reject(Err) );
		});
	}

	getAll(params){
		return new Promise((Resolve,Reject)=>{

			let {company_id} = params;
			
			// Add device name of handset to display in mobile application
			let Wh = {
				where:{ company_id: company_id },
				include: [{
					model: Models.handset,
					attributes: ['device_name']
				}],
				limit: params.limit,
				offset: params.offset,
			};

			return Models.user.findAndCountAll(Wh)
			.then((User)=>{				
				if (!User) return Reject('Non-existent User');
				return User;
			})
			.then( data => Resolve(data) )
			.catch( Err => Reject(Err) );
		});
	}

	edit(id,User){

		// If user updated from notify app
		if(id & parseInt(id) != 0){
			return new Promise((Resolve,Reject)=>{

				return Models.user.findById(id)
				.then((oldUser)=>{
					if (!oldUser) return Reject('Non-existent User');

					if( User.notifyUserId ){
						oldUser.notifyUserId = User.notifyUserId;
					}
					if( User.name ){
						oldUser.name = User.name;
					}
					if( User.role ){
						oldUser.role = User.role;
					}
					if( User.timezone_id ){
						oldUser.timezone_id = User.timezone_id;
					}

					oldUser.logged = ( User.logged );

					return oldUser.save();
				})
				.then( data => Resolve(data) )
				.catch( Err => Reject(Err) );
			});
		}

		// If user updated from admin panel
		else {
			return new Promise((Resolve,Reject)=>{

				let {notifyUserId=false,name=false,company_id=false} = User;
				if( !notifyUserId ) return Reject('Required param missing: "notifyUserId"');
				if( !name ) return Reject('Required param missing: "name"');
				if( !company_id ) return Reject('Required param missing: "company_id"');

				let Wh = {
					where: {
						notifyUserId	: parseInt(notifyUserId),
						company_id		: parseInt(company_id),
					},
				};

				return Models.user.findOne(Wh)
				.then((oldUser)=>{

					if (!oldUser) return Reject('Non-existent User');

					if( User.name ){
						oldUser.name = User.name;
					}
					if( User.role ){
						oldUser.role = User.role;
					}
					oldUser.save();
					
				})
				.then( data => Resolve(data))
				.catch( Err => Reject(Err) );
			});
		}
	}

	delete(params){

		return new Promise((Resolve,Reject)=>{

			let {notifyUserId=false,name=false,company_id=false} = params;
			if( !notifyUserId ) return Reject('Required param missing: "notifyUserId"');
			if( !name ) return Reject('Required param missing: "name"');
			if( !company_id ) return Reject('Required param missing: "company_id"');

			let Wh = {
				where: {
					notifyUserId	: notifyUserId,
					name			: name,
					company_id		: company_id,
				},
			};

			return Models.user.findOne(Wh)
			.then((condemnedUser)=>{
				if (!condemnedUser) return Reject('Non-existent User');

				return condemnedUser.destroy();
			})
			.then( data => Resolve(data) )
			.catch( Err => Reject(Err) );
		});
	}
}
