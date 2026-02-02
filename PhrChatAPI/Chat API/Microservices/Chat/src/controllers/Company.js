"use strict";
// only for debugging
// const debug	= require('../lib/debug');
const Models	= require('../models');

/* Company Controller Class */
module.exports = class controllerCompany{
	constructor(){
	}

	create(Company){
		return new Promise((Resolve,Reject)=>{
			return Models.company.create(Company)
			// Reserved for Append Namespace to socket.io
			// .then((company)=>{ /* Code */})
			// Reserved for Append Namespace to socket.io
			.then( data => Resolve(data) )
			.catch( Err => Reject(Err) );
		});
	}

	getOne(params){
		return new Promise((Resolve,Reject)=>{
			return Models.company.findById(params.id)
			.then((Company)=>{
				if (!Company) return Reject('Non-existent Company');
				return Company;
			})
			.then( data => Resolve(data) )
			.catch( Err => Reject(Err) );
		});
	}

	getAll(){
		return new Promise((Resolve,Reject)=>{
			let Wh = {
				// attributes: ['id','notifyCompanyId','name','param3','namespace'],
				limit: this.limit,
				offset: this.offset,
			};

			return Models.company.findAndCountAll(Wh)
			.then((Company)=>{
				if (!Company) return Reject('Non-existent Company');

				return Company;
			})
			.then( data => Resolve(data) )
			.catch( Err => Reject(Err) );
		});
	}

	edit(id,Company){
		return new Promise((Resolve,Reject)=>{
			return Models.company.findById(id)
			.then((oldCompany)=>{
				if (!oldCompany) return Reject('Non-existent Company');

				if( Company.notifyCompanyId ){
					oldCompany.notifyCompanyId = Company.notifyCompanyId;
				}
				if( Company.name ){
					oldCompany.name = Company.name;
				}
				if( Company.namespace ){
					oldCompany.namespace = Company.namespace;
				}

				return oldCompany.save();
			})
			.then( data => Resolve(data) )
			.catch( Err => Reject(Err) );
		});
	}

	delete(params,cb){
		return new Promise((Resolve,Reject)=>{
			return Models.company.findById(params.id)
			.then((condemnedCompany)=>{
				if (!condemnedCompany) return Reject('Non-existent Company');

				return condemnedCompany.destroy();
			})
			.then( data => Resolve(data) )
			.catch( Err => Reject(Err) );
		});
	}
}
