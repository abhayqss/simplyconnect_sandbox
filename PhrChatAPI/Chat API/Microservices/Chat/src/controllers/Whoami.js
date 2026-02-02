"use strict";
// only for debugging
// const debug		= require('../lib/debug');
const Models	= require('../models');
const userDto	= require('../dto/userDto');
const threadDto	= require('../dto/threadDto');

/* Whoami Controller Class */
module.exports = class controllerWhoami{
	constructor(){
	}

	get({user_id}){
		return new Promise((Resolve,Reject)=>{

			let Wh = {
				where: { id:user_id },
				attributes: ['id','notifyUserId','name','logged','role','company_id','timezone_id'],
				include: [{
					attributes: ['id','name','utc_offset','abbreviation'],
					model: Models.timezone,
				},{
					attributes: ['id','notifyCompanyId','namespace'],
					model: Models.company
				}]
			}

			return Models.user.findOne(Wh)
			.then( data => Resolve(data) )
			.catch( Err => Reject(Err) );
		});
	}

	getThreads(params){

		return new Promise((Resolve,Reject)=>{

			let {user_id} = params;
			let relatedThreads = [];
			let Wh = {
				where: { id:user_id },
				attributes: ['id','notifyUserId','name','logged','role','company_id','timezone_id'],
				include: [{
					as: 'threads',
					attributes: ['id','name','description','quantity'],
					model: Models.thread,
					required:true,
					include: [{
						as: 'thread_participants',
						attributes: ['id','name', 'notifyUserId'],
						model: Models.user,
						required:true,
					}],
				}]
			};
			//> Step1 Get USER
			return Models.user.findOne(Wh)
			.then((User)=>{
				if( User ) return User;

				// Remove include to just find User
				delete Wh.include;

				return Models.user.findOne(Wh)
				.then((User)=>{
					// user exists but does not have threads or not exists
					if( User ){
						let resUser	=  new userDto(User);
						return Resolve( resUser );
					} else {
						return Reject('Non-existent User');
					}
				})
				.catch(Err => Err);
			})
			.then((User)=>{
				// Step3 Get/COMPILE thread name
				return new userDto({
					id				: User.id,
					notifyUserId	: User.notifyUserId,
					name			: User.name,
					logged			: User.logged,
					role			: User.role,
					company_id		: User.company_id,
					timezone_id		: User.timezone_id,
					// parse threads data!
					threads			: User.threads.map((Thread)=>{
						// Store thread id for further purposes
						relatedThreads.push( Thread.id );

						return new threadDto( Thread , user_id, {unreadMessages:0,lastMessage:{}});
					}),
				});
			})
			.then((User)=>{

				let msgWh = {
					where: {
						// (QSS-Server) To get the latest message of user chat thread
						//sender		: { $ne: User.id },
						receiver	: relatedThreads,
					},
				};				

				// Step4 Get last Message
				return Models.thread_message.findAll(msgWh)
				.then((Messages)=>{
					// Step5 Get/count unread Messages
					Messages.forEach((message)=>{
						let i = User.threads.findIndex(i => i.id == message.receiver);

						// not found index stop processing this iteration
						if( i == -1 ) return;

						// (QSS-Server) Increase unread messages count if a message was not read
						if( !(message.deliveredAt) && (message.sender != User.id))	User.threads[i].unreadMessages++;

						// store last unread Message
						message.text = message.text ? message.text.toString('utf8') : message.text;
						User.threads[i].lastMessage = message;
					});

					return User;
				})
				.catch( Err => Reject(Err) );
			})
			.then((User)=>{
				// if no thread's just respond the User
				if( User.threads.length == 0 ) return User;
				
				
				//(QSS-Server) Remove chat of deleted user
				User.threads.forEach((userInfo, index)=>{
					if(!userInfo.name)
					{
						User.threads.splice(index, 1);
					}
				})

				//> Step6 Sort Threads by threads.createdAt DESC
				User.threads.sort((a,b)=>{
					let aTxt = a.lastMessage.text || null;
					let bTxt = b.lastMessage.text || null;

					if (aTxt && bTxt){
						let pDate = a.lastMessage.createdAt;
						let cDate = (b.lastMessage.createdAt || -1) ? b.lastMessage.createdAt : 0;
						return pDate < cDate ? 1 : -1;
					}else{
						return a.lastMessage.text ? -1 : 1;
					}
				});

				return User;
			})
			.then( data => Resolve(data) )
			.catch( Err => Reject(Err) );
		});
	}
}
