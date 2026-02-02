"use strict";
// only for debugging
const debug		= require('../lib/debug');
const isJson	= require('../lib/isJson');
const Models	= require('../models');
const bcrypt	= require('bcryptjs');


/* CompanyMigrate Controller Class */
module.exports = class controllerCompanyMigrate{
	constructor(){
	}

	validateTenantMaster(username=null,password=null){
		return new Promise((Resolve,Reject)=>{

			let Wh = {
				where: {username}
			};
			return Models.tenant_master.findOne( Wh )
			.then((data=null)=>{
				if( !data ) return Reject('Non-existent TenantMaster username');

				let credentials = ( bcrypt.compareSync( password,data.password ) );
				if( !credentials ) return Reject('TenantMaster bad credentials');

				return Resolve( credentials );
			})
			.catch( Err => Reject(Err) );
		});
	}

	createMigration(companyData){
		return new Promise((Resolve,Reject)=>{
			let {username=null,password=null} = companyData;

			this.validateTenantMaster(username,password)
			.then(()=>{

				let RES 		= {};
				let ERRORS		= [];
				let company		= isJson( companyData.company );
				let users		= isJson( companyData.users );
				let handsets	= isJson( companyData.handsets );

				if(!company) ERRORS.push({message:'company field must be JSON string type'});
				if(!users) ERRORS.push({message:'users field must be JSON string type'});
				if(!handsets) ERRORS.push({message:'handsets field must be JSON string type'});

				if( !Array.isArray(users) ) ERRORS.push({message:'users field must be an JSON stringified array'});
				if( !Array.isArray(handsets) ) ERRORS.push({message:'handsets field must be an JSON stringified array'});

				// Reject if data is not JSON
				if(ERRORS.length > 0) return Reject({message: 'Required params Missing',errors:ERRORS});

				return Models.company.create(company)
				.then((newCompany)=>{
					if( !newCompany ) return Reject('Error Inserting Company');

					global.ioHandler.nspAdd( newCompany.namespace );

					RES.company = newCompany;

					// prepare users to bulk insert
					return users.map((v,k)=>{
						return {
							notifyUserId: v.notifyUserId,
							name: v.name,
							role: v.role,
							company_id: newCompany.id,
							timezone_id: v.timezone_id,
						};
					});
				})
				.then((preparedUsers)=>{
					if(!preparedUsers) return Reject('Error Preparing Users');

					// Insert prepared users
					return Models.user.bulkCreate(preparedUsers)
					.then((newUsers)=>{ return newUsers; })
					.catch((Err)=>{ return Err; });
				})
				.then((newUsers)=>{
					if(!newUsers) return Reject('Error Inserting Users');

					RES.users = newUsers;

					// prepare handsets to bulk insert
					return handsets.map((v,k)=>{
						return {
							uuid: v.uuid,
							pn_token: v.pn_token,
							type: v.type,
							company_id: RES.company.id,
							device_name: v.name
						};
					});
				})
				.then((preparedHandsets)=>{
					if(!preparedHandsets) return Reject('Error Preparing Handsets');

					return Models.handset.bulkCreate(preparedHandsets)
					.then((newHandsets)=>{ return newHandsets; })
					.catch((Err)=>{ return Err; });
				})
				.then((newHandsets)=>{
					if(!newHandsets) return Reject('Error Inserting Handsets');

					RES.handsets = newHandsets;

					return RES;
				})
				.then( data => Resolve(data) )
				.catch( Err => Reject(Err) );
			})
			.catch( Err => Reject( Err ));
		});
	}


	resetMigration(params){
		return new Promise((Resolve,Reject)=>{

			let { name=null,namespace=null,password=null } = params;
			let Wh = {
				where: {name,namespace}
			};

			return Models.company.findOne( Wh )
			.then((company=null)=>{
				if(!company) return Reject('Non-existent Company');

				let credentials = ( bcrypt.compareSync( password,company.password ) );
				if( !credentials ) return Reject('company bad credentials');

				return company.destroy()
				.then( data => data )
				.catch( Err => Err );
			})
			.then( data => Resolve(data) )
			.catch( Err => Err )
		});
	}
}
