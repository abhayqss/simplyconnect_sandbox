"use strict";
// only for debugging
// const debug		= require('../lib/debug');
const Models	= require('../models');

/* Handset Controller Class */
module.exports = class controllerHandset{
	constructor(){
	}

	create(Handset){
		return new Promise((Resolve,Reject)=>{
			return Models.handset.create(Handset)
			.then( data => Resolve(data) )
			.catch( Err => Reject(Err) );
		});
	}

	getOne({id}){
		return new Promise((Resolve,Reject)=>{
			return Models.handset.findById(id)
			.then((Handset)=>{
				if (!Handset) return Reject('Non-existent Handset');
				return Handset;
			})
			.then( data => Resolve(data) )
			.catch( Err => Reject(Err) );
		});
	}

	getAll(){
		return new Promise((Resolve,Reject)=>{
			let Wh = {
				// attributes: ['id','Field1','name','param3','namespace'],
				limit: this.limit,
				offset: this.offset,
			};

			return Models.handset.findAndCountAll(Wh)
			.then((Handset)=>{
				if (!Handset) return Reject('Non-existent Handset');

				return Handset;
			})
			.then( data => Resolve(data) )
			.catch( Err => Reject(Err) );
		});
	}

	edit(id,Handset){
		return new Promise((Resolve,Reject)=>{
			return Models.handset.findById(id)
			.then((oldHandset)=>{
				if (!oldHandset) return Reject('Non-existent Handset');

				// Update handset UUID
				if( Handset.uuid ){
					oldHandset.uuid = Handset.uuid;
				}

				// Update handset Type
				if( Handset.type ){
					oldHandset.type = Handset.type;
				}

				return oldHandset.save();
			})
			.then( (data)=>{
				global.ioHandler.updateHandset(params['namespace'].toString(), data);
				Resolve(data);
			})
			.catch( Err => Reject(Err) );
		});
	}

	getHandsetByUUID(params){
		return new Promise((Resolve, Reject)=>{
			let {uuid} = params;
			let wh = {
				where: {uuid: uuid.toUpperCase()}
			}

			return Models.handset.findOne(wh)
				.then((handset)=>{
					if(handset){
						Resolve(handset);
					}
					else {
						Reject('Handset Already registered');
					}
				})
				.catch(Err => Reject(Err));
		})
	}

	updateHandsetByUUID(params){
		return new Promise((Resolve, Reject)=>{
			let {uuid} = params;
			let wh = {
				where: {uuid: uuid.toUpperCase()}
			}

			return Models.handset.findOne(wh)
				.then((handset)=>{

					if(!handset) return Reject('Non-existent Handset');
					
					if(params.pn_token){
						handset.pn_token = params.pn_token;
					}
					if( params.device_name ){
						handset.device_name = params.device_name;
					}
					if(params.type){
						handset.type = params.type;
					}
					if(params.company_id){
						handset.company_id = params.company_id;
					}
					
					handset.save();						
					Resolve(handset);
				})
				.then( (data) => {					
					global.ioHandler.updateHandset(params['namespace'].toString(), data);
					Resolve(data);
				})
				.catch( Err => Reject(Err) );
		});
	}


	updatePnToken({uuid=null,pn_token=null,type=null,company_id=null}){
		return new Promise((Resolve,Reject)=>{

			let Wh = {
				where: { uuid,type,company_id }
			};
			let value = { pn_token };

			return Models.handset.update( value , Wh )
			.then( data => Resolve(data) )
			.catch( Err => Reject(Err) );
		});
	}

	delete(params){

		return new Promise((Resolve,Reject)=>{

			let {uuid=false,type=false,company_id=false} = params;
			if( !uuid ) return Reject('Required param missing: "uuid"');
			if( !type ) return Reject('Required param missing: "type"');
			if( !company_id ) return Reject('Required param missing: "company_id"');

			//Find if user is logged in with this handset id
			let con = {
				where: {
					logged	: 1,
					current_handset: uuid.toUpperCase()
				}
			}

			return Models.user.findOne(con)
				.then((user)=>{
					if(user){
						user.logged = 0;
						user.current_handset = null;
						return user.save();
					}
				})
				.then((user)=>{
					let Wh = {
						where: {
							uuid		: uuid.toUpperCase(),
							type		: type,
							company_id	: company_id,
						},
					};
				return Models.handset.findOne(Wh)
					.then((condemnedHandset)=>{

						if (!condemnedHandset) return Reject('Non-existent Handset');

						return condemnedHandset.destroy();
					})
					.then( data => Resolve(data) )
					.catch( Err => Reject(Err) );
				})
		});
	}
}
