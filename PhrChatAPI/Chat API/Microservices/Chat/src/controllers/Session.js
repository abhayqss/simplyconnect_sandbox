"use strict";
// only for debugging
// const debug		= require('../lib/debug');
const JWT		= require('jsonwebtoken');
const bcrypt 	= require('bcryptjs');
const Models	= require('../models');

/* Session Controller Class */
module.exports = class controllerSession{
	constructor(){
		// Encode/Decode Options
		this.secretKey	= process.env.SECRET;
		this.options	= { expiresIn: process.env.JWT_EXPIRATION };
	}

	create(Session){

		return new Promise((Resolve,Reject)=>{
			if( Session.namespace && Session.password ){
				return this.createCompanyToken(Session)
				.then( data => Resolve(data) )
				.catch( Err => Reject(Err) );
			}
			// Handler for Regular Chat User
			else {
				return this.createChatUserToken(Session)
				.then( data => Resolve(data) )
				.catch( Err => Reject(Err) );
			}
		});
	}

	createChatUserToken(Session){
		return new Promise((Resolve,Reject)=>{
			let updateDevToken	= (Session.deviceToken) ? Session.deviceToken : false;
			let Wh = {
				where: {
					id: parseInt(Session.company_id)
				},
				include: [{
					model: Models.user,
					where: {
						notifyUserId: parseInt(Session.notifyUserId),
						company_id: parseInt(Session.company_id),
						// logged: false,
					},
					required: true,
				},
				{
					model: Models.handset,
					where: {
						uuid: Session.uuid,
						type: Session.type,
						company_id: parseInt(Session.company_id),
					},
					required: true,
				}]
			};
			
			return Models.company.findOne(Wh)
			.then((availableAut)=>{	
				if( !availableAut ) return Reject('Cannot authenticate with provided parameters');				

				let Company	= {
					id: availableAut.id,
					notifyCompanyId: availableAut.notifyCompanyId,
					namespace: availableAut.namespace
				};
				let User = availableAut.users.shift();
				let Handset = availableAut.handsets.shift();

				let Value = { outTime: new Date() };
				let hWhere = {
					where: {
						company_id: Session.company_id,
						user_id: User.id,
						outTime: null
					}
				};

				// Close all Prev Sessions
				return Models.session_history.update( Value , hWhere )
				.then((updatedHistories)=>{ return {Company:Company,User:User,Handset:Handset,Stories:updatedHistories}; })
				.catch( Err => Reject(Err) );

			})
			.then((data)=>{	
				// update if deviceToken was received
				if(!(updateDevToken)) return data;

				let {Company,User,Handset,Stories} = data;
				
				

				return Models.handset.update({pn_token:updateDevToken},{where:{id:Handset.id}})
				.then((updHs)=>{ return {Company:Company,User:User,Handset:Handset,Stories:Stories}; })
				.catch( Err => Reject(Err) );

			})
			.then((data)=>{	
				let {Company,User,Handset} = data;
				let insH = {
					user_id: User.id,
					handset_id: Handset.id,
					company_id: Handset.company_id,
					ip:	Session.ip,
				};

				// Create new Session History entry
				return Models.session_history.create(insH)
				.then((newHistory)=>{ return {Company:Company,User:User,Handset:Handset,History:newHistory} })
				.catch( Err => Reject(Err) );
			})
			.then((data)=>{

				let {Company,User,Handset,History} = data;

				// update User Status and current Handset
				User.logged = true;
				User.current_handset = Session.uuid;

				return User.save()
				.then((updUser)=>{ return {Company:Company,User:User,Handset:Handset,History:History}; })
				.catch( Err => Reject(Err) );
			})
			.then((data)=>{

				let {Company,User,Handset,History} = data;

				// prepare Token Payload
				let Res = {
					User: User,
					tokenPayload: {
						company_id		: Company.id,
						namespace		: Company.namespace,
						role			: User.role,
						user_id			: User.id,
						handset_id		: Handset.id,
						handset_uuid	: Handset.uuid,
						history_id		: History.id,
					},
					whoami: {
						id: User.id,
						notifyUserId: User.notifyUserId,
						name: User.name,
						role: User.role,
						company: {
							id: Company.id,
							notifyCompanyId: Company.notifyCompanyId,
							namespace: Company.namespace,
						},
						timezone_id: User.timezone_id,
					}
				};

				return Res;
			})
			/* Add user To available users in chat */
			.then((data)=>{
				let {User, tokenPayload} = data;
				let updateDevToken	= (Session.deviceToken) ? Session.deviceToken : false;

				// (QSS-Server) To send the device handset information to the mobile app
				let Wh = {
					attributes: ['device_name'],
					where: {
						uuid: User.current_handset
					}
				};
				Models.handset.findOne(Wh)
				.then(function(handset){
					global.ioHandler.addUser(tokenPayload.namespace,{id:User.id,name:User.name, handset: handset});

				})

				let Res = { tokenPayload: tokenPayload };
				Res.whoami = data.whoami;

				return Res;
			})
			/* Add user To available users in chat */
			.then((data)=>{
				let {tokenPayload} = data;
				if( !tokenPayload ) return Reject('Payload not generated');

				// Create Token and Return Token
				let Res = { token: JWT.sign(tokenPayload,this.secretKey,this.options),whoami:data.whoami };

				return Res;
			})
			.then( data => Resolve(data) )
			.catch( Err => Reject(Err) );
		});
	}

	createCompanyToken(Session){
		return new Promise((Resolve,Reject)=>{
			let { namespace=null,password=null } = Session;
			let Wh = { where: { namespace } };

			return Models.company.findOne(Wh)
			.then((company=null)=>{
				if( !company ) return Reject('Non-existent company');
				if( !bcrypt.compareSync( Session.password , company.password ) ) return Reject('Incorrect credentials');

				return company;
			})
			.then((company)=>{
				// Prepare token payload
				return {
					id: company.id,
					namespace: company.namespace,
					role: 'ROOT',
				};
			})
			.then((tokenPayload=null)=>{
				if( !tokenPayload ) return Reject('Payload not generated');
				
				return {token:JWT.sign(tokenPayload,this.secretKey,this.options),whoami: Session.whoami};
			})
			.then( data => Resolve(data) )
			.catch( Err => Reject(Err) );
		});
	}

	validate(Token){
		
		return new Promise((Resolve,Reject)=>{
			if(!Token){
				return Reject ('You must provide a token to access this endpoint');
			}

			return JWT.verify(Token,this.secretKey,(err,decodedToken)=>{
				if( err || !(decodedToken)) return Reject ('invalid Token');

				if( decodedToken.id && decodedToken.namespace && decodedToken.role ){
					return this.validateCompanyUser(decodedToken)
					.then( data => Resolve(data) )
					.catch( Err => Reject(Err) );
				}

				return this.validateChatUser(decodedToken)
				.then( data => Resolve(data) )
				.catch( Err => Reject(Err) );
			});
		});
	}

	validateChatUser(decodedToken){

		return new Promise((Resolve,Reject)=>{
			if(!decodedToken) return Reject('no Decoded token was received');

			let Wh = {
				where: {
					id			: decodedToken.company_id,
					namespace	: decodedToken.namespace,
				},
				include: [
					{
						model	: Models.user,
						required: true,
						where	: {
							id				: decodedToken.user_id,
							role			: decodedToken.role,
							logged			: (true),
							current_handset	: decodedToken.handset_uuid,
						},
					},
					{
						model	: Models.handset,
						required: true,
						where	: {
							id		: decodedToken.handset_id,
							uuid	: decodedToken.handset_uuid,
						},
					}
				],
			};

			return Models.company.findOne(Wh)
			.then((Session=null)=>{
				
				if( !(Session) ) return Reject('Invalid token');

				return {
					decodedToken: decodedToken,
					session: Session,
				};
			})
			.then( data => Resolve(data) )
			.catch( Err => Reject(Err) );
		});
	}

	validateCompanyUser(decodedToken){
		return new Promise((Resolve,Reject)=>{
			if(!decodedToken) return Reject('no Decoded token was received');
			if( decodedToken.role != 'ROOT' ) return Reject("you don't have enough rights for this task");

			let Wh = {
				where: {
					id: decodedToken.id,
					namespace: decodedToken.namespace,
				}
			};


			return Models.company.findOne(Wh)
			.then((Session)=>{
				if (!Session) return Reject('Invalid token');

				return {
					decodedToken: decodedToken,
					session: Session,
				};
			})
			.then( data => Resolve(data) )
			.catch( Err => Reject(Err) );
		});
	}

	delete(Params){
		return new Promise((Resolve,Reject)=>{
			return Models.session_history.findById(Params.history_id)
			.then((Session)=>{
				if (!Session) return Reject('Invalid token');

				Session.outTime = new Date();

				return Session.save();
			})
			.then((data)=>{
				if(!data) return Reject('Error Deleting session history');

				return Models.user.update({logged:false,current_handset:null},{where:{id:Params.user_id}})
				.then((some)=>{
					return some;
				})
				.catch( Err => Reject(Err) );
			})
			/* Drop user from online users in chat */
			.then((User)=>{
				global.ioHandler.removeUser(Params.namespace,{id:Params.user_id});
				return User;
			})
			.then( data => Resolve(data) )
			.catch( Err => Reject(Err) );
		});
	}

	forcedRemoval({company_id = null,notifyUserId = null,uuid = null,type = null}){
		return new Promise((Resolve,Reject)=>{
			return Models.user.findOne({where: {company_id,notifyUserId}})
			.then((User=null)=>{
				if(!(User)) return Reject('Non-Existent user');

				return User;
			})
			.then(User=>{
				return Models.handset.findOne({where:{uuid,company_id}})
				.then((Handset=null)=>{
					if(!(Handset)) return Reject('Non-Existent handset');

					return {User:User,Handset:Handset};
				})
				.catch( Err => Reject(Err) );
			})
			.then(({User,Handset})=>{
				return Models.company.findById(company_id)
				.then((Company=null)=>{
					if(!(Company)) return Reject('Non-Existent company');

					return {User,Handset,Company};
				})
				.catch( Err => Reject(Err) );
			})
			.then(({User,Handset,Company})=>{
				/* Unset handset from old user  */
				return Models.user.update({current_handset:null},{where:{current_handset:Handset.uuid}})
				.then(data=>{
					return {User,Handset,Company};
				})
				.catch( Err => Reject(Err) );
			})
			.then(({User,Handset,Company})=>{

				// update user
				User.logged = false;
				User.current_handset = null;

				return User.save()
				.then( updUser => {

					let historyData = {
						outTime	:new Date(),
					};
					let historyWh = {
						where: {
							user_id		: User.id,
							company_id	: Company.id,
							handset_id	: Handset.id,
						}
					};

					// delete session_history
					return Models.session_history.update(historyData,historyWh)
					.then((data)=>{
						/* Drop user from online users in chat */
						return global.ioHandler.removeUser(Company.namespace,{id:User.id});
					})
					.catch( Err => Reject(Err) );
				})
				.catch( Err => Reject(Err) );
			})
			.then( data => Resolve(data) )
			.catch( Err => Reject(Err) );
		});
	}
}
